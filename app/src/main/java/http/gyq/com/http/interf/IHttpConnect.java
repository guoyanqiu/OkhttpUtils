package http.gyq.com.http.interf;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;


import http.gyq.com.http.request.HttpRequest;

/**
 * 表示一Http请求链接
 * Created by Arthur on 2017/8/7.
 */

public interface IHttpConnect {
    /**
     * 异步请求
     */
    void asyncConnect(@NonNull HttpRequest request);

    /**
     * 同步链接请求,不能在主线程中调用，只能在异步线程中，并且注意同步引起的性能问题
     */
    @WorkerThread
    IResponse syncConnect(@NonNull HttpRequest request) throws Exception;

    void abort(HttpRequest request);

    void abortAll();
}
