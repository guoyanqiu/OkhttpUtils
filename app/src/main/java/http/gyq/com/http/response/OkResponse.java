package http.gyq.com.http.response;

import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.request.HttpRequest;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Arthur on 2017/8/7.
 */

public class OkResponse implements IResponse {
    private Response okResponse;
    private HttpRequest httpRequest;
    public OkResponse(Response okResponse, HttpRequest httpRequest) {
        this.okResponse = okResponse;
        this.httpRequest = httpRequest;
    }

    public HttpRequest getRequest() {
        return httpRequest;
    }
    @Override
    public Map<String, List<String>> getHeaders() {
        if (okResponse == null) {
            return null;
        }
        Headers headers = okResponse.headers();
        return headers == null ? null : headers.toMultimap();
    }

    @Override
    public ResponseBody getResponseBody() {
        if (okResponse == null) {
            return null;
        }
        return okResponse.body();
    }

    @Override
    public String getString() {
        ResponseBody body = getResponseBody();
        if(body == null) {
            return "";
        }
        try {
            return body.string();
        }catch (IOException e) {
            return "";
        }
    }

    @Override
    public int getCode() {
        if(okResponse == null){
            return 0;
        }
        return okResponse.code();
    }

    /**
     * 下载用
     * @return
     */
    @Nullable
    @Override
    public InputStream getInputStream() {
        ResponseBody body = getResponseBody();
        if (body == null) {
            return null;
        }
        return body.byteStream();
    }

    @Override
    public long getContentLength() {
        if (okResponse == null) {
            return -1;
        }
        ResponseBody body = getResponseBody();
        if (body == null) {
            return -1;
        }
        return body.contentLength();
    }

    @Override
    public void close() {
        if (okResponse == null) {
            return;
        }

        okResponse.close();
    }

}
