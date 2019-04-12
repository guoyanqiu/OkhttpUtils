package http.gyq.com.http.request;

import android.support.annotation.Nullable;

/**
 * 发送json串的请求
 */
public class JsonRequest extends Request {
    //请求参数
    @Nullable
    private String json;

    public JsonRequest(String url) {
        super(url);
    }

    @Nullable
    public String getJson() {
        return json;
    }

    public void setJson(@Nullable String json) {
        this.json = json;
    }
}
