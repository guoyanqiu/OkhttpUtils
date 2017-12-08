package http.gyq.com.http.request;

/**
 * Created by Arthur on 2017/8/7.
 */

public class RequestID {
    private static int _ID = Integer.MIN_VALUE;
    /**
     * 此处获取动态ID
     * @return
     */
    public static int createID() {
        _ID = _ID++;
        if (_ID >= Integer.MAX_VALUE) {
            _ID = Integer.MIN_VALUE;
            createID();
        }
        return _ID;
    }
}
