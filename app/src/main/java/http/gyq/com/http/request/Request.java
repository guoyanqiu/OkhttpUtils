package http.gyq.com.http.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import http.gyq.com.http.interf.IRequestCallback;

public  class Request {
    @NonNull
    protected String url;
    //请求头信息
    @Nullable
    private Map<String, String> headers;
    //请求类型:默认为get请求类型
    protected RequestType requestType = RequestType.GET;
    @Nullable
    private IRequestCallback requestCallback;
    private long requestId = 0;

    public Request(String url){
        this.url = url;
        if(isRequestValidity(this)){
            throw new IllegalArgumentException("url 拼写错误");
        }
        requestId = System.currentTimeMillis()+new Random().nextInt(100);
    }

    //请求参数
    @Nullable
    private Map<String, String> params;

    public Map<String, String> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return Collections.unmodifiableMap(params);
    }

    public boolean hasParams() {
        return params != null && !params.isEmpty();
    }

    public String getUrl(){
        if(requestType==RequestType.GET){
            url = buildUrlWithParams(url,params);
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

    public IRequestCallback getCallback() {
        return requestCallback;
    }

    public boolean hasHeaders() {
        return headers != null && !headers.isEmpty();
    }
    public RequestType getReqeustType() {
        return requestType;
    }

    public long getRequestId() {
        return requestId;
    }

    public Map<String, String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<>();
        }
        return Collections.unmodifiableMap(headers);
    }

    public Request setUrl(@NonNull String url) {
        this.url = url;
        return this;
    }

    public Request setRequestType(RequestType requestType) {
        this.requestType = requestType;
        return this;
    }

    public Request setCallback(@Nullable IRequestCallback requestCallback) {
        this.requestCallback = requestCallback;
        return this;
    }

    public boolean isRequestValidity(Request request) {
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
    protected String parseUrl(@NonNull String url) {
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
    public Request addHeader(String name, String value) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(name, value);
        return this;
    }

    public Request addParams(String name, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(name, value);
        return this;
    }
    public Request addHeaders(Map<String, String> headers) {
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

    public Request addParams(Map<String, String> params) {
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

}
