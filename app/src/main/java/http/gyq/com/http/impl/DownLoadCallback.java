package http.gyq.com.http.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import http.gyq.com.http.interf.IResponse;
import http.gyq.com.http.request.Request;

/**
 * 文件下载功能
 * Created by Arthur on 2017/8/8.
 */

public class DownLoadCallback extends RequestCallbackImpl {
    /**
     * 目标文件存储的文件夹路径
     */
    private String filePath;
    private String fileName;
    private DownloaderListener downloaderListener;

    public DownLoadCallback(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public void setDownloadListener(DownloaderListener downloaderListener) {
        this.downloaderListener = downloaderListener;
    }

    @Override
    public void requestFinish(Request request, IResponse response) {
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = response.getInputStream();
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            fos = new FileOutputStream(file);
            int len = 0;
            byte[] buf = new byte[2048];
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            //下载成功
            if (downloaderListener != null) {
                downloaderListener.downloadSuccess(file.getAbsolutePath());
            }

        } catch (IOException e) {
            if (downloaderListener != null) {
                downloaderListener.downloadFailed(e);
            }

        } finally {
            try {
                response.close();
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }//end finally
    }

    public interface DownloaderListener {
        void downloadSuccess(String filePath);

        void downloadFailed(Exception exception);
    }
}
