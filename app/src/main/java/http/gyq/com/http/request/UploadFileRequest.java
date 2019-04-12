package http.gyq.com.http.request;


import android.support.annotation.NonNull;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 上传文件的请求
 * Created by Arthur on 2017/8/8.
 */

public class UploadFileRequest extends Request {
    //form的name
    private List<FileInfo> mFileInfos;

    public List<FileInfo> getFiles() {
        return Collections.unmodifiableList(mFileInfos);
    }


    public UploadFileRequest(String url) {
        super(url);
        this.mFileInfos = new ArrayList<>();
    }

    public UploadFileRequest addFiles(List<FileInfo> files) {
        if (files != null && !files.isEmpty()) {
            mFileInfos.addAll(files);
        }
        return this;
    }


    public UploadFileRequest addFile(@NonNull FileInfo fileInfo) {
        mFileInfos.add(fileInfo);
        return this;
    }
    public static String getFileContentType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 每个文件上传的最小单元
     */
    public static class FileInfo {
        //类别表单中<input type="file" name="mFile"/>的name属性。
        @NonNull
        public String name;
        @NonNull
        public String fileName;
        @NonNull
        public File file;

        public FileInfo() {
        }

        public FileInfo(@NonNull String name, @NonNull String fileName, @NonNull File file) {
            this.name = name;
            this.fileName = fileName;
            this.file = file;
        }
    }

    public static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;
        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


}
