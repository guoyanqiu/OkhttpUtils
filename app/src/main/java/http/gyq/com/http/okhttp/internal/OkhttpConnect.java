package http.gyq.com.http.okhttp.internal;

import android.support.annotation.NonNull;


import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public OkhttpConnect() {
    }

    @Override
    public void asyncConnect(@NonNull final Request request) {
        final okhttp3.Request.Builder builder = OkHttpUitls.createOkHttpRequestBuilder(request);
        final IRequestCallback handler = request.getCallback();
        if (handler != null) {
            handler.startRequest(request);
        }
        Call call = OkhttpClientUtil.getInstance().newCall(builder.build());

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                OkResponse okHttpResponse = new OkResponse(response, request);
                if (handler != null && response.isSuccessful()) {
                    handler.requestFinish(request, okHttpResponse);
                }
            }
            @Override
            public void onFailure(Call call, IOException e) {
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
        final okhttp3.Request.Builder builder =  OkHttpUitls.createOkHttpRequestBuilder(request);
        IRequestCallback handler = request.getCallback();
        if (handler != null) {
            handler.startRequest(request);
        }
        Response response = OkhttpClientUtil.getInstance().newCall(builder.build()).execute();
        return new OkResponse(response, request);
    }

    @Override
    public void abort(Request request) {
        OkhttpClientUtil.getInstance().cancelTag(request.getRequestId());
    }

    @Override
    public void abortAll() {
        OkhttpClientUtil.getInstance().abortAll();
    }



}
