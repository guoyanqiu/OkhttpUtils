package http.gyq.com.http.okhttp.internal;

import android.support.annotation.NonNull;


import java.io.File;
import java.util.List;
import java.util.Map;

import http.gyq.com.http.interf.IHttpConnect;
import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.okhttp.OkHttpHelper;
import http.gyq.com.http.request.HttpRequest;
import http.gyq.com.http.request.RequestType;
import http.gyq.com.http.request.UploadFileRequest;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 创建OKhttp的链接
 * Created by Arthur on 2017/8/7.
 */

public class OkhttpConnect implements IHttpConnect {
    private OkHttpHelper okHttpHelper;

    public OkhttpConnect(OkHttpHelper okHttpHelper) {
        this.okHttpHelper = okHttpHelper;
    }

    @Override
    public void asyncConnect(@NonNull HttpRequest request) {
        final okhttp3.Request.Builder builder = createOkHttpRequestBuilder(request);
        okHttpHelper.startAsyncConnect(builder.build(), request);
    }

    @Override
    public IResponse syncConnect(@NonNull HttpRequest request) {
        final okhttp3.Request.Builder builder = createOkHttpRequestBuilder(request);
        return okHttpHelper.startSyncConnect(builder.build(), request);
    }

    @Override
    public void abort(HttpRequest request) {
        okHttpHelper.abortRequest(request);
    }

    @Override
    public void abortAll() {
        okHttpHelper.abortAll();
    }

    private okhttp3.Request.Builder createOkHttpRequestBuilder(final HttpRequest request) {
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

    private RequestBody createBody(HttpRequest httpRequest) {
        RequestBody requestBody = null;
        if (httpRequest instanceof UploadFileRequest) {//文件上传
            requestBody = createMultiBody((UploadFileRequest) httpRequest);

        } else if (httpRequest.getJson() != null) {
            MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
            System.out.println("请求json==" + httpRequest.getJson());
            requestBody = RequestBody.create(jsonType, httpRequest.getJson());
        } else {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            if (httpRequest.hasParams()) {
                for (Map.Entry<String, String> entry : httpRequest.getParams().entrySet()) {
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
