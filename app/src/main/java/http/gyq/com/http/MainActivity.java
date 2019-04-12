package http.gyq.com.http;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import http.gyq.com.http.impl.RequestCallbackImpl;
import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.request.Request;
import http.gyq.com.http.request.RequestType;
import http.gyq.com.http.request.UploadFileRequest;

public class MainActivity extends AppCompatActivity {
    private ConnectHelper connectHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectHelper = new ConnectHelper();
        get();


    }

    private void get() {

       Request request= new Request("http://www.baidu.com");
        request.addHeader("xxx", "yyyy")
                .addParams("xxx", "yyyy")
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(Request request, IResponse response) {
                        System.out.print("respose ==" + response.getString());

                    }
                });
        connectHelper.asyncConnect(request);
    }

    public void delete() {
        Request request= new Request("http://www.baidu.com");
        request
                .setRequestType(RequestType.DELETE)
                .addHeader("xxx", "yyyy")
                .addParams("xxx", "yyyy")
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(Request request, IResponse response) {

                    }
                });
        connectHelper.asyncConnect(request);
    }

    public void post() {
        Request request= new Request("http://www.baidu.com");
        request.setRequestType(RequestType.POST)
                .addHeader("xxx", "yyyy")
                .addParams("xxx", "yyyy")
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(Request request, IResponse response) {

                    }
                });
        connectHelper.asyncConnect(request);

        //同步请求
        try {
            IResponse response = connectHelper.syncConnect(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void put() {
        Request request= new Request("http://www.baidu.com");
        request.setRequestType(RequestType.PUT)
                .addHeader("xxx", "yyyy")
                .addParams("xxx", "yyyy")
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(Request request, IResponse response) {

                    }
                });
        connectHelper.asyncConnect(request);
    }

    public void upload() {

        UploadFileRequest.FileInfo f1 = new UploadFileRequest.FileInfo("", "", new File("."));
        UploadFileRequest request = new UploadFileRequest("http://www.baidu.com");

        request.addFile(f1).addFile(f1).addFile(f1)
                .addFiles(new ArrayList<UploadFileRequest.FileInfo>())
                .addParams("xxx", "yyyy")
                .setRequestType(RequestType.POST)
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(Request request, IResponse response) {

                    }
                });

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
//
//        //创建jsonType
//        MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
//
//        //创建json请求体
//        RequestBody jsonBody = RequestBody.create(jsonType, new JSONObject().toString());
//
//        Request.Builder builder = new Request.Builder();
//
//        //post提交jsonBody
//        builder.post(jsonBody);

    }
}
