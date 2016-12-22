package cn.mqclient.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;


import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.mqclient.App;
import cn.mqclient.Layer.common.PSubject;

import cn.mqclient.Log;
import cn.mqclient.async.tool.AsyncSession;
import cn.mqclient.async.tool.Request;
import cn.mqclient.async.tool.SingleAsyncSession;
import cn.mqclient.entity.ComponentArray;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.entity.PlayLog;
import cn.mqclient.entity.http.BaseEntity;
import cn.mqclient.model.LogRequest;
import cn.mqclient.model.base.BaseRequest;
import cn.mqclient.model.base.RequestListener;
import cn.mqclient.provider.Layer;
import cn.mqclient.provider.OpLayerDao;
import cn.mqclient.provider.ReadLayerDao;
import cn.mqclient.provider.log.OpLogDao;
import cn.mqclient.provider.log.ReadLogDao;
import cn.mqclient.utils.AppUtils;
import cn.mqclient.utils.KeepAliveTask;
import cn.mqclient.utils.ModuleFile;
import cn.mqclient.utils.ScreenTextManager;
import cn.mqclient.utils.SerializableFile;
import cn.mqclient.utils.SharePref;
import cn.mqclient.utils.SpConstants;
import cn.mqclient.utils.ThreadPool;
import okhttp3.Response;

/**
 * Created by LinZaixiong on 2016/11/14.
 */

public class TimerService extends IntentService {
    private final static String TEXT = "text";
    public static final long MILLISTIME = 5 * 1000; // 5秒读一次
    public static final long SENDLOGTIME = 1000 * 60 * 5;//60 *  60 * 4; // 4个 小时发送一次
    public static final long DELETELOGTIME = 1000 * 60 * 60 * 24 * 14; // 2个 星期
    private boolean isRunning = true;
    private static String TEXT_CONTENT = "";
    private KeepAliveTask keepAlive = new KeepAliveTask();
    private ScreenTextManager textManager = new ScreenTextManager();
    private long mSendLogTime = 0;
    private long mDeleteLogTime = 0;
    public static void start(Context context) {
        startWithText(context, null);
    }

    public static void startWithText(Context context, String text) {
        Intent starter = new Intent(context, TimerService.class);
        starter.putExtra(TEXT, text);
        context.startService(starter);
    }
    public static void stop(Context context) {
        Log.d("jacklam", " stop TimerService");
        Intent starter = new Intent(context, TimerService.class);
        context.stopService(starter);
    }

    public TimerService(){
        super("DownloadService");
    }

    @Override
    public void onCreate() {
        Log.d("service", "onCreate:" + this);
        if(keepAlive != null)
            keepAlive.createKeepAlive(this, this, "惟石", "惟石定时服务", KeepAliveTask.NOTIFICATION_ID_TIMER);
        if(textManager != null){
            textManager.destroyScreen(this);
            textManager.initScreen(this);
        }
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        if(textManager != null){
            textManager.startScreen(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("service", "onDestroy:" + this);
        super.onDestroy();
        isRunning = false;

        if(keepAlive != null)
            keepAlive.destroyKeepService(this, this, KeepAliveTask.NOTIFICATION_ID_TIMER);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("service", "onHandleIntent:" + this);
        String text = intent.getStringExtra(TEXT);
        if(TextUtils.isEmpty(text)){
            if(textManager != null){
                if(TextUtils.isEmpty(TEXT_CONTENT)){
                    TEXT_CONTENT = SharePref.getInstance().getString("counting", "");
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textManager.setText(App.getInstance(), TEXT_CONTENT);
                    }
                });


            }
            process();
        }
        else{
            TEXT_CONTENT = text;
            if(textManager != null){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textManager.setText(App.getInstance(), TEXT_CONTENT);
                    }
                });
            }
        }

    }

    private void process() {

        while(isRunning){

            try {
                Thread.sleep(MILLISTIME);
                sendLog();
                clearLog();
                readDb();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void sendLog() {
        if(mSendLogTime != 0 && System.currentTimeMillis() - mSendLogTime >=  SENDLOGTIME){
            mSendLogTime = System.currentTimeMillis();
            Log.d("jacklam", "sendlog");
            ThreadPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    List<cn.mqclient.provider.log.Log> mLogs =
                            ReadLogDao.getInstance(App.getInstance()).readSendLog(SharePref.getInstance().getString(SpConstants.WARRANTNO, ""));
                    if(mLogs != null){
                        PlayLog log = new PlayLog();
                        log.setWARRANTNO(SharePref.getInstance().getString(SpConstants.WARRANTNO, ""));
                        log.setSeNumber(AppUtils.getSeNumber(App.getInstance()));
                        List<PlayLog.PlayLogItem> mItems = new ArrayList<PlayLog.PlayLogItem>();
                        for(int i = 0; i < mLogs.size(); i++){
                            PlayLog.PlayLogItem item = new PlayLog.PlayLogItem();
                            item.setId(mLogs.get(i).getId());
                            if(mLogs.get(i).getBroadcastStartTime()!= null){
                                item.setBroadcastStartTime(mLogs.get(i).getBroadcastStartTime().getTime());
                            }

                            if(mLogs.get(i).getExitTime()!= null)
                                item.setBroadcastEndTime(mLogs.get(i).getExitTime().getTime());

                            item.setTerminalGroupItemName(mLogs.get(i).getTerminalGroupItemName());
                            item.setTimes(mLogs.get(i).getTimes());
                            mItems.add(item);
                        }
                        log.setList(mItems);

                        LogRequest request = new LogRequest(new RequestListener() {
                            @Override
                            public void success(BaseRequest baseRequest, Response response, BaseEntity baseEntity) {
                                Log.d("jacklam", "sendlog success");
                            }

                            @Override
                            public void failed(BaseRequest baseRequest, String s) {
                                Log.d("jacklam", "sendlog failed:" + s);
                                Log.d("jacklam", "sendlog failed");
                            }
                        });
                        request.setParams(JSON.toJSONString(log));
                        Log.d("jacklam", "sendlog:" + JSON.toJSONString(log));
                        request.start();
                    }

                }
            });
        }

        if(mSendLogTime == 0)
            mSendLogTime = System.currentTimeMillis();

    }

    private void clearLog() {

        if(mDeleteLogTime != 0 && System.currentTimeMillis() - mDeleteLogTime >=  DELETELOGTIME) {
            mDeleteLogTime = System.currentTimeMillis();
            ThreadPool.getInstance().submit(new Runnable() {
                @Override
                public void run() {
                    OpLogDao.getInstance(App.getInstance()).clearAll();

                }
            });

        }
        if(mDeleteLogTime == 0)
            mDeleteLogTime = System.currentTimeMillis();
    }


    private void readDb(){
        Date date  = new Date(System.currentTimeMillis());
        List<Layer> list = ReadLayerDao.getInstance(App.getInstance()).read(date);

        if(list != null){

            for(int i = 0; i < list.size(); i++){
                List<ComponentData> clist = JSON.parseArray(list.get(i).getJson(), ComponentData.class);

                if(clist != null){

//                    clist.getData().get(0).setId(list.get(i).getId());
                    OpLayerDao.getInstance(App.getInstance()).delete(list.get(i));

                    ModuleFile file = new ModuleFile("");
                    file.write(list.get(i).getName(), JSON.toJSONString(list.get(i)));

                    PSubject.getInstance().notify(list.get(i),
                            clist, list.get(i).getName());

                }

            }
        }


    }
}
