package http.gyq.com.http.okhttp.internal;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import http.gyq.com.http.interf.IRequestCallback;
import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.interf.Response;
import http.gyq.com.http.request.JsonRequest;
import http.gyq.com.http.request.Request;
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
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;

/**
 * 类比retrofit OkhttpCall
 */
public class OkhttpManager<Bean> {
    public void abort(Request request) {
        OkhttpClientUtil.getInstance().cancelTag(request.getRequestId());
    }

    public void abortAll() {
        OkhttpClientUtil.getInstance().abortAll();
    }

    static ResponseBody buffer(final ResponseBody body) throws IOException {
        Buffer buffer = new Buffer();
        body.source().readAll(buffer);
        return ResponseBody.create(body.contentType(), body.contentLength(), buffer);
    }

    Response<Bean> parseResponse(okhttp3.Response rawResponse) throws IOException {
        ResponseBody rawBody = rawResponse.body();

        // Remove the body's source (the only stateful object) so we can pass the response along.
        rawResponse = rawResponse.newBuilder()
                .body(new NoContentResponseBody(rawBody.contentType(), rawBody.contentLength()))
                .build();

        int code = rawResponse.code();
        if (code < 200 || code >= 300) {
            try {
                // Buffer the entire body to avoid future I/O.
                ResponseBody bufferedBody = buffer(rawBody);
                return Response.error(bufferedBody, rawResponse);
            } finally {
                rawBody.close();
            }
        }

        if (code == 204 || code == 205) {
            return Response.success(null, rawResponse);
        }

        ExceptionCatchingRequestBody catchingBody = new ExceptionCatchingRequestBody(rawBody);
        try {
            Gson gson = new Gson();
            //可以放在Request 获取Bean的信息
            TypeAdapter<Bean> adapter = gson.getAdapter(TypeToken.get(Bean));
            JsonReader jsonReader = gson.newJsonReader(catchingBody.charStream());
            try {
                Bean body =adapter.read(jsonReader);
                return Response.success(body, rawResponse);
            } finally {
                catchingBody.close();
            }
        } catch (RuntimeException e) {
            catchingBody.throwIfCaught();
            throw e;
        }
    }

    public Response<Bean> execute(@NonNull Request request) throws IOException {
        synchronized (this) {
            if (request.isExecuted()) throw new IllegalStateException("Already executed.");
            request.setExecuted(true);
        }

        final okhttp3.Request.Builder builder = createOkHttpRequestBuilder(request);
        final IRequestCallback handler = request.getCallback();
        if (handler != null) {
            handler.startRequest(request);
        }
        Call call = OkhttpClientUtil.getInstance().newCall(builder.build());

        return parseResponse(call.execute());
    }

    static final class ExceptionCatchingRequestBody extends ResponseBody {
        private final ResponseBody delegate;
        IOException thrownException;

        ExceptionCatchingRequestBody(ResponseBody delegate) {
            this.delegate = delegate;
        }

        @Override
        public MediaType contentType() {
            return delegate.contentType();
        }

        @Override
        public long contentLength() {
            return delegate.contentLength();
        }

        @Override
        public BufferedSource source() {
            return Okio.buffer(new ForwardingSource(delegate.source()) {
                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    try {
                        return super.read(sink, byteCount);
                    } catch (IOException e) {
                        thrownException = e;
                        throw e;
                    }
                }
            });
        }

        @Override
        public void close() {
            delegate.close();
        }

        void throwIfCaught() throws IOException {
            if (thrownException != null) {
                throw thrownException;
            }
        }
    }

    static final class NoContentResponseBody extends ResponseBody {
        private final MediaType contentType;
        private final long contentLength;

        NoContentResponseBody(MediaType contentType, long contentLength) {
            this.contentType = contentType;
            this.contentLength = contentLength;
        }

        @Override
        public MediaType contentType() {
            return contentType;
        }

        @Override
        public long contentLength() {
            return contentLength;
        }

        @Override
        public BufferedSource source() {
            throw new IllegalStateException("Cannot read raw response body of a converted body.");
        }
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
