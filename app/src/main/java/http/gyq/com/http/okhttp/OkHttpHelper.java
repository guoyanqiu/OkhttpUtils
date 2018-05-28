package http.gyq.com.http.okhttp;

import android.util.SparseArray;

import java.io.IOException;

import http.gyq.com.http.interf.IRequestCallback;
import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.okhttp.internal.OkhttpClientUtil;
import http.gyq.com.http.request.HttpRequest;
import http.gyq.com.http.response.OkResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Arthur on 2017/8/7.
 */

public class OkHttpHelper {
    public OkHttpHelper() {

    }

    /**
     * 发起异步请求
     *
     * @param okRequest
     * @param request
     */
    public void startAsyncConnect(final okhttp3.Request okRequest,
                                  final HttpRequest request) {
        final IRequestCallback handler = request.getCallback();
        if (handler != null) {
            handler.startRequest(request);
        }
        Call call = OkhttpClientUtil.getInstance().newCall(okRequest);

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

    /**
     * 发起同步请求
     *
     * @param okRequest
     * @param request
     * @return
     */
    public IResponse startSyncConnect(okhttp3.Request okRequest, HttpRequest request) throws IOException {
        IRequestCallback handler = request.getCallback();
        if (handler != null) {
            handler.startRequest(request);
        }
        Response response = OkhttpClientUtil.getInstance().newCall(okRequest).execute();
        return new OkResponse(response, request);


    }

    public void abortRequest(HttpRequest httpRequest) {

        OkhttpClientUtil.getInstance().cancelTag(httpRequest.getRequestId());
    }

    public void abortAll() {
        OkhttpClientUtil.getInstance().abortAll();
    }

}
