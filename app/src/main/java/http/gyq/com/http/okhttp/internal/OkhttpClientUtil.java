package http.gyq.com.http.okhttp.internal;


import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Arthur on 2017/7/24.
 */

public class OkhttpClientUtil {
    public static final int TIME_OUT = 15;
    private OkHttpClient client;
    private volatile static OkhttpClientUtil sInstance;

    public OkhttpClientUtil(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();
        } else {
            client = okHttpClient;
        }
    }


    public static OkhttpClientUtil initClient(OkHttpClient okHttpClient) {
        if (sInstance == null) {
            synchronized (OkhttpClientUtil.class) {
                if (sInstance == null) {
                    sInstance = new OkhttpClientUtil(okHttpClient);
                }
            }
        }
        return sInstance;
    }

    public Call newCall(Request request) {
        return client.newCall(request);
    }

    public static OkhttpClientUtil getInstance() {
        return initClient(null);
    }


    public void abortAll() {
        client.dispatcher().cancelAll();
    }

    public void cancelTag(Object tag) {
        Dispatcher dispatcher = client.dispatcher();
        for (Call call : dispatcher.queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : dispatcher.runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

}
