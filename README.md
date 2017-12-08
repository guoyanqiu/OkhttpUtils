# OkhttpUtils
对Okhttp的简单封装，可以满足基本的get,post.put,delete请求

//使用代码示例
public class MainActivity extends AppCompatActivity {
    //初始化一个ConnectHelper用来处理请求
    private ConnectHelper connectHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectHelper = new ConnectHelper();

    }

    //get请求示例
    private void get() {
        //创建builder来创建request对象
        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setUrl("http://www.baidu.com")
                .addHeader("xxx", "yyyy")
                .addParams("xxx", "yyyy")
                .setCallback(new RequestCallbackImpl() {
                    @Override
                    public void requestFinish(HttpRequest request, IResponse response) {
                        //请求回调

                    }
                });
        //创建请求
        HttpRequest request = builder.build();
        //异步发送请求
        connectHelper.asyncConnect(request);
        //同步发送请求
        connectHelper.syncConnect(request);

    }

    //delete请求示例
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

    //post请求代码示例
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

    //put请求代码示例
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

    //文件双穿
    public void upload() {

        //创建一个文件请求的bean
        UploadFileRequest.FileInfo f1 = new UploadFileRequest.FileInfo("name", "fileName", new File("."));

        //创建文件请求的builder对象
        UploadFileRequest.UploadBuilder builder = new UploadFileRequest.UploadBuilder();

        //可以通过addFile的方式一个个添加，也可以通过addFiles添加一个集合
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
    }
}

