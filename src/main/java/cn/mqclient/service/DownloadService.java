package cn.mqclient.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import cn.mqclient.Log;

import java.util.ArrayList;
import java.util.List;

import cn.mqclient.App;
import cn.mqclient.http.download.base.DownloadEntity;
import cn.mqclient.http.download.base.DownloadManager;
import cn.mqclient.http.download.base.DownloadTask;
import cn.mqclient.utils.Md5;

/**
 * Created by LinZaixiong on 2016/10/20.
 */

public class DownloadService extends Service {
    public static final String TAG = "DownloadService";
    public static final String DOWNLOAD_TASK_SERIALIZABLE = "DOWNLOAD_TASK_SERIALIZABLE";

    public static void   startDownloadService(Context conetxt, DownloadEntity entity){

        if(entity != null){
            Log.d(TAG, "startDownloadService");
            Intent intent = new Intent(conetxt, DownloadService.class);
            intent.putExtra(DOWNLOAD_TASK_SERIALIZABLE, entity);
            conetxt.startService(intent);
        }
    }

//    public DownloadService(){
//        super("DownloadService");
//    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
//    public DownloadService(String name) {
//        super(name);
//    }

    @Override
    public void onCreate() {
        Log.d(TAG, "startDownloadService onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "startDownloadService onStartCommand");
        onHandleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "startDownloadService onHandleIntent");
        process(intent);
    }

    private void process(Intent intent){
        Log.d(TAG, "startDownloadService process");
        DownloadEntity entity = (DownloadEntity)intent.getSerializableExtra(DOWNLOAD_TASK_SERIALIZABLE);
        // 传空，字串为空， 监听器为空都不启动
        if(entity != null &&
                entity.getTaskListener() != null){
            Log.d(TAG, "startTask URL" + entity.getUrl());
            if(!TextUtils.isEmpty(entity.getUrl())){
                Log.d(TAG, "startTask");
                startTask(entity);
            }
            else{
                Log.d(TAG, "URL EMPTY");
                if(entity.getTaskListener() != null){
                    entity.getTaskListener().onError(null, 404);
                }
            }

        }


    }

    private void startTask(DownloadEntity entity){

        DownloadTask task = generateDownloadTask(entity);
        // 已经下载完成的
        if(!isDownloaded(entity.getUrl())){
            Log.d(TAG, "isDownloaded FALSE");
            DownloadManager.getInstance(App.getInstance()).addDownloadTask(task, entity.getTaskListener());
        }
        else{
            Log.d(TAG, "isDownloaded TRUE");
            if(entity.getTaskListener() != null){
                entity.getTaskListener().onCompleted(task);
            }
        }
    }


    private DownloadTask generateDownloadTask(DownloadEntity entity){

        DownloadTask task = new DownloadTask();
        String fileName = Md5.MD5(entity.getUrl());
        String suffix = entity.getUrl().substring(entity.getUrl().lastIndexOf("."));
        task.setFileName(fileName + suffix);
        task.setIndex(entity.getIndex());
        task.setId(entity.getUrl());
        task.setAction(entity.getAction());
        task.setSaveDirPath(Environment.getExternalStorageDirectory().getPath()+ "/mqclient/" + fileName + "/");
        task.setUrl(entity.getUrl());

        return task;
    }

    private boolean isDownloaded(String url){

        boolean isRet = false;
//        isRet = DownloadManager.getInstance(App.getInstance()).isDownloaded(url);
        return isRet;
    }
}
