package http.gyq.com.http;

import android.support.annotation.NonNull;

import http.gyq.com.http.interf.IHttpConnect;
import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.okhttp.internal.OkhttpConnect;
import http.gyq.com.http.request.Request;

/**
 * Created by Arthur on 2017/8/7.
 */

public class ConnectHelper implements IHttpConnect {
    private IHttpConnect httpConnect;
    public ConnectHelper () {
        httpConnect = new OkhttpConnect();
    }

    /**
     * 发起异步网络而链接
     * @param request
     */
    public void asyncConnect(@NonNull Request request) {
        //无效的请求
        if (!request.isRequestValidity(request)) {
            return;
        }
        httpConnect.asyncConnect(request);
    }

    /**
     * 发起同步网络链接
     * @param request
     * @return
     */
    public IResponse syncConnect(@NonNull Request request) throws Exception {
        //无效的请求
        if (!request.isRequestValidity(request)) {
            return null;
        }
        return httpConnect.syncConnect(request);

    }

    @Override
    public void abort(Request request) {
        httpConnect.abort(request);
    }


    @Override
    public void abortAll() {
        httpConnect.abortAll();
    }
}
