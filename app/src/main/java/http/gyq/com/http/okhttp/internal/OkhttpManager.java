package http.gyq.com.http.okhttp.internal;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import http.gyq.com.http.interf.IRequestCallback;
import http.gyq.com.http.interf.Response;
import http.gyq.com.http.request.Request;
import okhttp3.Call;
import okhttp3.MediaType;
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
                ResponseBody bufferedBody = OkHttpUitls.buffer(rawBody);
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

        final okhttp3.Request.Builder builder = OkHttpUitls.createOkHttpRequestBuilder(request);
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

}
