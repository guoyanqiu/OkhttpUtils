package http.gyq.com.http.impl;

import android.support.annotation.Nullable;

import http.gyq.com.http.interf.IRequestCallback;
import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.request.HttpRequest;

/**
 * Created by Arthur on 2017/8/8.
 */

public class RequestCallbackImpl implements IRequestCallback {

    @Override
    public void requestFinish(HttpRequest request, IResponse response) {

    }

    @Override
    public void requestError(HttpRequest request, @Nullable Exception e) {

    }

    @Override
    public void startRequest(HttpRequest request) {

    }
}