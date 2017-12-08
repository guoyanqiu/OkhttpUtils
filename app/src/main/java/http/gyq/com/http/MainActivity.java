package http.gyq.com.http;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import http.gyq.com.http.impl.RequestCallbackImpl;
import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.okhttp.Constant;
import http.gyq.com.http.request.HttpRequest;
import http.gyq.com.http.request.RequestType;
import http.gyq.com.http.request.UploadFileRequest;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {
    private ConnectHelper connectHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectHelper = new ConnectHelper();
//        get();
        Map<String, String> map = new HashMap<String, String>();
        map.put("method", "nbls.open.user.login");
        map.put("app_key", "pm4s7fflq5dq2e1w");
        map.put("sign_method", "md5");
        map.put("timestamp", DateUtil.getCurrentTime());
        map.put("format", "json");
        map.put("v", "1.0.0");
        map.put("partner_id", "200002");
        map.put("device_no", android.os.Build.ID);
        map.put("device_type", "android");
        map.put("system_version", android.os.Build.VERSION.RELEASE);
        map.put("app_version", "1.0");
//        map.put("userId","");
//        map.put("token","");

        Log.i("constant", Constant.sort(map));

        map.put("sign", Constant.getMD5Str(Constant.sort(map)));
        Log.i("constant", Constant.getMD5Str(Constant.sort(map)));

        String aaa = "";
        try {
            byte[] aa = Constant.encryptByte("657255", "2688spor");
            aaa = Constant.bytesToString(aa);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, Object> basemap = new HashMap<>();
        basemap.put("baseRequest", map);
        HashMap<String, String> datamap = new HashMap<>();
        datamap.put("userName", "13911657255");
        datamap.put("passWord", aaa);
        basemap.put("datar", datamap);
        Gson gson = new Gson();
        String json = gson.toJson(basemap);
//        Log.i("baseRequest",json);
        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setUrl("http://sport.teamhealth.com.cn/open-api/user/login")
                .setRequestType(RequestType.POST)
//                .setJson(json)
//                .addHeader("content-type", "application/json;charset:utf-8")
                .addParams("json", json)
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(HttpRequest request, IResponse response) {
                        Log.i("response", "网络请求＝" + response.getString());
                    }

                    @Override
                    public void requestError(HttpRequest request, @Nullable Exception e) {
                        super.requestError(request, e);
                    }
                });
        HttpRequest request = builder.build();
        connectHelper.asyncConnect(request);

    }

    private void get() {
        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setUrl("http://www.baidu.com")
                .addHeader("xxx", "yyyy")
                .addParams("xxx", "yyyy")
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(HttpRequest request, IResponse response) {
                        System.out.print("respose ==" + response.getString());

                    }
                });
        HttpRequest request = builder.build();
        connectHelper.asyncConnect(request);
    }

    public void delete() {
        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setUrl("http://www.baidu.com")
                .setRequestType(RequestType.DELETE)
                .addHeader("xxx", "yyyy")
                .addParams("xxx", "yyyy")
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(HttpRequest request, IResponse response) {

                    }
                });
        HttpRequest request = builder.build();
        connectHelper.asyncConnect(request);
    }

    public void post() {
        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setUrl("http://www.baidu.com")
                .setRequestType(RequestType.POST)
                .addHeader("xxx", "yyyy")
                .addParams("xxx", "yyyy")
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(HttpRequest request, IResponse response) {

                    }
                });
        HttpRequest request = builder.build();
        connectHelper.asyncConnect(request);
    }

    public void put() {
        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setUrl("http://www.baidu.com")
                .setRequestType(RequestType.PUT)
                .addHeader("xxx", "yyyy")
                .addParams("xxx", "yyyy")
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(HttpRequest request, IResponse response) {

                    }
                });
        HttpRequest request = builder.build();
        connectHelper.asyncConnect(request);
    }

    public void upload() {

        UploadFileRequest.FileInfo f1 = new UploadFileRequest.FileInfo("", "", new File("."));

        UploadFileRequest.UploadBuilder builder = new UploadFileRequest.UploadBuilder();

        builder.addFile(f1).addFile(f1).addFile(f1)
                .addFiles(new ArrayList<UploadFileRequest.FileInfo>())
                .setUrl("http://www.baidu.com")
                .addParams("xxx", "yyyy")
                .setRequestType(RequestType.POST)
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(HttpRequest request, IResponse response) {

                    }
                });

        HttpRequest request = builder.build();
        connectHelper.asyncConnect(request);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectHelper.abortAll();

        //创建jsonType
        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");

        //创建json请求体
        RequestBody jsonBody = RequestBody.create(jsonType, new JSONObject().toString());

        Request.Builder builder = new Request.Builder();

        //post提交jsonBody
        builder.post(jsonBody);

    }
}
