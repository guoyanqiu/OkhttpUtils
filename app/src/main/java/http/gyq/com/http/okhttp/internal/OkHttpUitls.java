package http.gyq.com.http.okhttp.internal;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import http.gyq.com.http.request.JsonRequest;
import http.gyq.com.http.request.Request;
import http.gyq.com.http.request.RequestType;
import http.gyq.com.http.request.UploadFileRequest;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;

class OkHttpUitls {

    public static okhttp3.Request.Builder createOkHttpRequestBuilder(final Request request) {
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

    private static RequestBody  createBody(Request request) {
        RequestBody requestBody = null;
        if (request instanceof UploadFileRequest) {//文件上传
            requestBody = createMultiBody((UploadFileRequest) request);
        } else if (request instanceof JsonRequest) {
            JsonRequest jsonRequest = (JsonRequest) request;
            MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
            requestBody = RequestBody.create(jsonType, jsonRequest.getJson());
        } else {
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

    private static MultipartBody createMultiBody(UploadFileRequest fileRequest) {
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
    public static ResponseBody buffer(final ResponseBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.source().readAll(buffer);
        return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
    }
}
