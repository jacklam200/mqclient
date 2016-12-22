package cn.mqclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import cn.mqclient.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.mqclient.http.ProgressDownloader;
import cn.mqclient.http.ProgressResponseBody;
import cn.mqclient.http.download.base.DownloadManager;
import cn.mqclient.http.download.base.DownloadStatus;
import cn.mqclient.http.download.base.DownloadTask;
import cn.mqclient.http.download.base.DownloadTaskListener;
import cn.mqclient.utils.Md5;

/**
 * Created by LinZaixiong on 2016/9/11.
 */
public class DownloadActivity extends BaseActivity{
    public static final String TAG = "MainActivity";
    public static final String PACKAGE_URL = "http://gdown.baidu.com/data/wisegame/df65a597122796a4/weixin_821.apk";
    ProgressBar progressBar;
    private long breakPoints;
    private ProgressDownloader downloader;
    private File file;
    private long totalBytes;
    private long contentLength;
    private DownloadManager downloadManager;
    private Handler handler;
    public static void start(Context context) {

        Intent starter = new Intent(context, DownloadActivity.class);
        context.startActivity(starter);
    }

    public void onStop(View v){
        downloadManager.cancel(PACKAGE_URL);

    }

    public void onContinue(View v){
        DownloadTask downloadTask = downloadManager.getCurrentTaskById(PACKAGE_URL);
        if(downloadTask.getDownloadStatus()== DownloadStatus.DOWNLOAD_STATUS_PAUSE){
            downloadManager.resume(PACKAGE_URL);
        }else{
            downloadManager.pause(PACKAGE_URL);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        handler = new Handler();
        downloadManager = DownloadManager.getInstance(getApplicationContext());
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        ((Button)findViewById(R.id.bt_status)).setText("sdcard" + "/");
    }
    private List<String> taskIds = new ArrayList<>();
    public void onDownload(View view) {
        DownloadTask task = new DownloadTask();
        String fileName = Md5.MD5(PACKAGE_URL);

        task.setFileName(fileName+".apk");
        taskIds.add(PACKAGE_URL);
        task.setId(PACKAGE_URL);
        task.setSaveDirPath("sdcard"/*getExternalCacheDir().getPath() */+ "/");

        task.setUrl(PACKAGE_URL);
        downloadManager.addDownloadTask(task, new DownloadTaskListener() {
            @Override
            public void onPrepare(final DownloadTask downloadTask) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        ((Button)findViewById(R.id.bt_status)).setText("preparing ");

//                        setNotification(downloadTask.getFileName(),
//                                "Will start to download counter " + counter, downloadTask.getId().hashCode());

                    }
                });
                Log.d(TAG,"onPrepare");
            }

            @Override
            public void onStart(final DownloadTask downloadTask) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((Button)findViewById(R.id.bt_status)).setText("start ");

                    }
                });
                Log.d(TAG,"onStart");

            }

            @Override
            public void onDownloading(final DownloadTask downloadTask) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((Button)findViewById(R.id.bt_status)).setText((int) downloadTask.getPercent() + "%       ");
                       progressBar.setProgress((int) downloadTask.getPercent());

//                        updateNotification(downloadTask.getFileName(),
//                                (int) downloadTask.getPercent(), downloadTask.getToolSize(), downloadTask.getId().hashCode());

                    }
                });
                Log.d(TAG,"onDownloading");
            }

            @Override
            public void onPause(DownloadTask downloadTask) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((Button)findViewById(R.id.bt_status)).setText("onPause ");
                        Toast.makeText(getApplicationContext(),"onPause",Toast.LENGTH_LONG).show();
                    }
                });
                Log.d(TAG,"onPause");

            }

            @Override
            public void onCancel(DownloadTask downloadTask) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((Button)findViewById(R.id.bt_status)).setText("onCancel ");
                        Toast.makeText(getApplicationContext(),"onCancel",Toast.LENGTH_LONG).show();
                    }
                });
                Log.d(TAG,"onCancel");

            }

            @Override
            public void onCompleted(final DownloadTask downloadTask) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((Button)findViewById(R.id.bt_status)).setText("onCompleted ");
//                        finishedDownload(downloadTask.getId().hashCode());

                    }
                });
                Log.d(TAG,"onCompleted");

            }

            @Override
            public void onError(DownloadTask downloadTask, int errorCode) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((Button)findViewById(R.id.bt_status)).setText("error ");

                    }
                });
                Log.d(TAG,"onError");

            }
        });


    }



}
