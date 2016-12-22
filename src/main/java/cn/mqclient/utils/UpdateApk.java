package cn.mqclient.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

import static android.net.sip.SipErrorCode.TIME_OUT;

/**
 * Created by Kanwah on 2016/12/16.
 */

public class UpdateApk {

    public static interface  OnUpdateComplete{
        void onComplete(int code, String path);
    }

    final static int TIME_OUT = 8000;


    public static int getVersionNameFromApk(Context context, String archiveFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        int version = packInfo.versionCode;
        return version;
    }

    public static boolean update(final String url, final String path, final OnUpdateComplete listener){
        boolean isRet = false;
        ThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                FileOutputStream os = null;
                File file = null;
                HttpURLConnection mConnection = null;

                try {
                    mConnection = openConnectionGet(url, TIME_OUT);
                    int code = mConnection.getResponseCode();

                    if (code != HttpURLConnection.HTTP_OK
                            && code != HttpURLConnection.HTTP_MOVED_TEMP) {
                        throw new IOException("http response error: " + String.valueOf(code));
                    }

                    int mContentLength = (int) mConnection.getContentLength();
                    String fullFileName;
                    is = mConnection.getInputStream();
                    File f = new File(path);
                    if (f.exists()) {
                        f.delete();
                    }
//                    File dir = new File(mUpdateProperty.getSDCardSavePath());
//                    if (!dir.exists())
//                        dir.mkdirs();
                    os = new FileOutputStream(path);
                    copyStream(is, os);
                    os.close();
                    if(listener != null){
                        listener.onComplete(0, path);
                    }
                    return ;
                } catch (Exception e) {
                    return ;
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                            is = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (mConnection != null)
                        mConnection.disconnect();
                    mConnection = null;
                }
            }
        });
        return isRet;
    }

    private static void copyStream(InputStream inStream, OutputStream outStream) throws Exception {
        int len = -1;
        int total = 0;
        byte[] buffer = new byte[1024];
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
            total += len;
            //float tempLength=(total/mContentLength)*100;
            //pd.setProgress((int)tempLength);
        }
    }

    private static HttpURLConnection openConnectionGet(String url, int timeOut) {
        HttpURLConnection httpConn = null;
        try {
            String encodedUrl = Uri.encode(url, ":/?&=@");
            URL urlObj = new URL(encodedUrl);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            //httpConn.setDoOutput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("GET");
            httpConn.setConnectTimeout(timeOut);
            httpConn.setReadTimeout(timeOut);
            httpConn.setRequestProperty("Charset", "UTF-8");
            httpConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            //httpConn.setRequestProperty("Connection", "close");
            return httpConn;
        } catch (Exception ex) {
            return null;
        } finally {
            return httpConn;
        }
    }
}
