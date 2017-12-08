package http.gyq.com.http.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import http.gyq.com.http.interf.IRequestCallback;

/**
 * Created by Arthur on 2017/8/7.
 */

public class HttpRequest {
    @NonNull
    private String url;
    //请求参数
    @Nullable
    private Map<String, String> params;

    @Nullable
    public String getJson() {
        return json;
    }

    //请求参数
    @Nullable
    private String json;
    //请求头信息
    @Nullable
    private Map<String, String> headers;
    //请求类型:默认为get请求类型
    private RequestType requestType = RequestType.GET;
    @Nullable
    private IRequestCallback requestCallback;
    private int requestId = 0;

    public HttpRequest(@NonNull Builder builder) {
        this.url = builder.url;
        this.params = builder.params;
        this.headers = builder.headers;
        this.requestType = builder.requestType;
        this.requestCallback = builder.requestCallback;
        this.json = builder.json;
        this.requestId = RequestID.createID();
        if (RequestType.GET == requestType) {
            this.url = buildUrlWithParams(url, params);
        }
    }

    public int getRequestId() {
        return requestId;
    }

    public RequestType getReqeustType() {
        return requestType;
    }

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return Collections.unmodifiableMap(headers);
    }

    public Map<String, String> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return Collections.unmodifiableMap(params);
    }

    public boolean hasHeaders() {
        return headers != null && !headers.isEmpty();
    }

    public boolean hasParams() {
        return params != null && !params.isEmpty();
    }

    public IRequestCallback getCallback() {
        return requestCallback;
    }

    public String getUrl() {
        return url;
    }

    public boolean isReuestValidity(HttpRequest request) {
        if (request == null) {
            return false;
        }
        if (TextUtils.isEmpty(request.url) || TextUtils.isEmpty(request.url.trim())) {
            return false;
        }
        request.url = parseUrl(request.url);
        if (TextUtils.isEmpty(request.url)) {
            return false;
        }
        return true;
    }

    private String parseUrl(@NonNull String url) {
        try {
            URL urlParse = new URL(url);
            String host = urlParse.getHost();
            if (TextUtils.isEmpty(host)) {
                return null;
            }
        } catch (Exception e) {
        }
        return url;
    }

    public static String buildUrlWithParams(String url, Map<String, String> params) {

        if (params == null) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (!url.contains("?")) {
            sb.append("?");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() != null) {
                sb.append(entry.getKey());
                sb.append("=");
                sb.append(entry.getValue());
                sb.append("&");
            }
        }
        if (params.size() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }

    public static class Builder {

        @NonNull
        private String url;
        //请求参数
        @Nullable
        private Map<String, String> params;
        //请求头信息
        @Nullable
        private Map<String, String> headers;
        //请求类型:默认为get请求类型
        private RequestType requestType = RequestType.GET;
        //参数json字符串类型
        @Nullable
        private String json;
        @Nullable
        private IRequestCallback requestCallback;

        public Builder() {

        }

        public Builder setJson(String json) {
            this.json = json;
            return this;
        }


        public Builder setCallback(IRequestCallback requestCallback) {
            this.requestCallback = requestCallback;
            return this;
        }

        public Builder setUrl(@NonNull String url) {
            this.url = url;
            return this;
        }

        public String getJson() {
            return json;
        }

        public Builder addHeader(String name, String value) {
            if (headers == null) {
                headers = new HashMap<>();
            }
            headers.put(name, value);
            return this;
        }

        public Builder addParams(String name, String value) {
            if (params == null) {
                params = new HashMap<>();
            }
            params.put(name, value);
            return this;
        }

        public Builder setRequestType(RequestType requestType) {
            if (requestType == null) {
                return this;
            }
            this.requestType = requestType;
            return this;
        }

        public Builder setHeaders(Map<String, String> headers) {
            if (headers == null || headers.isEmpty()) {
                return this;
            }
            if (this.headers == null) {
                this.headers = headers;
            } else {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    if (entry.getValue() != null) {
                        this.headers.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            return this;
        }

        public Builder setParams(Map<String, String> params) {
            if (params == null || params.isEmpty()) {
                return this;
            }
            if (this.headers == null) {
                this.params = params;
            } else {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    if (entry.getValue() != null) {
                        this.params.put(entry.getKey(), entry.getValue());
                    }
                }
            }
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }

    }

}
