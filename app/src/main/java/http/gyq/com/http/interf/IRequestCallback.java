package http.gyq.com.http.interf;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import http.gyq.com.http.request.Request;

/**
 * Created by Arthur on 2017/8/7.
 */

public interface IRequestCallback {

    void requestFinish(@NonNull Request request, IResponse response);

    void requestError(@NonNull Request request, @Nullable Exception e);

    void startRequest(@NonNull Request request);
}
