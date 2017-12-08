package http.gyq.com.http.interf;

import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * Created by Arthur on 2017/8/7.
 */

public interface IResponse  {
    /**
     * 获取服务器返回的header
     * @return
     */
    @Nullable
    <Headers> Headers getHeaders();

    /**
     * 获取响应内容
     * @return
     */
    @Deprecated
    @Nullable<ResponseBody> ResponseBody getResponseBody();

    /**
     * 获取服务器响应的状态码
     * @return
     */
    int getCode();
    /**
     * 获取响应内容构成的字符串
     * @return
     */
    @Nullable String getString();

    /**
     * 获取网络请求返回的输出流，用于下载等功能
     * @return
     */
    @Nullable
    InputStream getInputStream();

    long getContentLength();

    void close();

}
