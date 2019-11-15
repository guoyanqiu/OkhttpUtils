package http.gyq.com.http.okhttp.internal;

import android.support.annotation.NonNull;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import http.gyq.com.http.interf.IHttpConnect;
import http.gyq.com.http.interf.IRequestCallback;
import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.request.Request;
import http.gyq.com.http.request.JsonRequest;
import http.gyq.com.http.request.RequestType;
import http.gyq.com.http.request.UploadFileRequest;
import http.gyq.com.http.response.OkResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 创建OKhttp的链接
 * Created by Arthur on 2017/8/7.
 */

public class OkhttpConnect implements IHttpConnect {
    private ConcurrentHashMap<String, Request> httpMap = new ConcurrentHashMap<>();
    public OkhttpConnect() {
    }

    @Override
    public void asyncConnect(@NonNull final Request request) {
        final String requestId = request.getRequestId();
        if (httpMap.contains(requestId)) {
            //取消之前的请求
            abort(request);
        }
        httpMap.put(request.getRequestId(),request);
        final okhttp3.Request.Builder builder = createOkHttpRequestBuilder(request);
        final IRequestCallback handler = request.getCallback();
        if (handler != null) {
            handler.startRequest(request);
        }

        Call call = OkhttpClientUtil.getInstance().newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                httpMap.remove(requestId);
                OkResponse okHttpResponse = new OkResponse(response, request);
                if(handler==null){
                    return;
                }
                if (response.isSuccessful()) {
                    handler.requestFinish(request, okHttpResponse);
                }else{
                    handler.requestError(request, new Exception("http response not in [200--299] "));
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
                httpMap.remove(requestId);
                if (call.isCanceled()) {
                    return;
                }
                if (handler != null) {
                    handler.requestError(request, e);
                }
            }
        });
    }

    @Override
    public IResponse syncConnect(@NonNull Request request) throws IOException {
        final String requestId = request.getRequestId();
        if (httpMap.contains(requestId)) {
            //取消之前的请求
            abort(request);
        }
        httpMap.put(request.getRequestId(),request);

        final okhttp3.Request.Builder builder = createOkHttpRequestBuilder(request);
        IRequestCallback handler = request.getCallback();
        if (handler != null) {
            handler.startRequest(request);
        }
        Response response = OkhttpClientUtil.getInstance().newCall(builder.build()).execute();
        httpMap.remove(requestId);
        return new OkResponse(response, request);
    }

    @Override
    public void abort(Request request) {
        final String requestId = request.getRequestId();
        httpMap.remove(requestId);
        OkhttpClientUtil.getInstance().cancelTag(requestId);
    }

    @Override
    public void abortAll() {
        OkhttpClientUtil okHttpUtils = OkhttpClientUtil.getInstance();
        for(Map.Entry entry:httpMap.entrySet()){
            Request request = (Request) entry.getKey();
            String requestId = request.getRequestId();
            okHttpUtils.cancelTag(requestId);
        }
        httpMap.clear();
    }

    private okhttp3.Request.Builder createOkHttpRequestBuilder(final Request request) {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        builder.tag(request.getRequestId());
        builder.url(request.getUrl());

        if (request.hasHeaders()) {
            for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
                if (entry.getValue() != null) {
                    builder.addHeader(entry.getKey(), entry.getValue());
                }
            }
        }
        RequestType type = request.getReqeustType();
        if (type == RequestType.GET) {
            return builder;
        } else {
            //构建请求体
            RequestBody body = createBody(request);
            switch (type) {
                case POST:
                    builder.post(body);
                    break;
                case PUT:
                    builder.put(body);
                    break;
                case DELETE:
                    builder.delete(body);
                    break;
            }

        }
        return builder;
    }

    private RequestBody createBody(Request request) {
        RequestBody requestBody = null;
       if (request instanceof UploadFileRequest) {//文件上传
            requestBody = createMultiBody((UploadFileRequest) request);
        } else if (request instanceof JsonRequest) {
            JsonRequest jsonRequest = (JsonRequest) request;
            MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
            requestBody = RequestBody.create(jsonType, jsonRequest.getJson());
        }else{
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (request.hasParams()) {
                for (Map.Entry<String, String> entry : request.getParams().entrySet()) {
                    if (entry.getValue() != null) {
                        formBodyBuilder.add(entry.getKey(), entry.getValue());
                    }
                }
            }
            requestBody = formBodyBuilder.build();
        }
        return requestBody;
    }

    private MultipartBody createMultiBody(UploadFileRequest fileRequest) {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        List<UploadFileRequest.FileInfo> fileInfos = fileRequest.getFiles();
        if (fileInfos != null) {
            for (int i = 0, size = fileInfos.size(); i < size; i++) {
                UploadFileRequest.FileInfo fileInfo = fileInfos.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse(UploadFileRequest.guessMimeType(fileInfo.fileName)), fileInfo.file);
                builder.addFormDataPart(fileInfo.name, fileInfo.fileName, fileBody);
            }
        }

        if (fileRequest.hasParams()) {
            for (Map.Entry<String, String> entry : fileRequest.getParams().entrySet()) {
                if (entry.getValue() != null) {
                    builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create(null, entry.getValue()));
                }
            }
        }
        return builder.build();
    }


}
