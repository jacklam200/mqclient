package cn.mqclient.Layer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import cn.mqclient.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.mqclient.App;
import cn.mqclient.Layer.common.DownloadListener;
import cn.mqclient.Layer.common.InactivityTimer;
import cn.mqclient.Layer.common.Observer;
import cn.mqclient.Layer.common.PSubject;
import cn.mqclient.R;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.entity.PieceMaterialModel;
import cn.mqclient.http.download.base.DownloadEntity;
import cn.mqclient.http.download.base.DownloadManager;
import cn.mqclient.http.download.base.DownloadStatus;
import cn.mqclient.http.download.base.DownloadTask;
import cn.mqclient.http.download.base.DownloadTaskListener;
import cn.mqclient.provider.Layer;
import cn.mqclient.provider.log.OpLogDao;
import cn.mqclient.provider.log.ReadLogDao;
import cn.mqclient.service.DownloadService;
import cn.mqclient.utils.LogRecorder;
import cn.mqclient.utils.Md5;
import cn.mqclient.utils.SharePref;
import cn.mqclient.utils.SpConstants;
import cn.mqclient.utils.XScreen;
import cn.mqclient.widget.IViewLifeCycle;

/**
 * 需要清除操作，比如关闭视频
 * Created by LinZaixiong on 2016/9/9.
 */
public abstract class BaseProcessor<T extends View>  implements Serializable, IViewLifeCycle, Observer<Layer, ComponentData>, InactivityTimer.OnTimer {
    public static final int KEY_TAG = R.id.tag_processor;
    public static final int MSG_WHAT = 0x01;
//    private transient List<String> taskIds = new ArrayList<>();
    protected  XScreen mScreen;
    protected transient Context mContext;
    private float WIDTH = 1080;
    private float HEIGHT = 1920;
    protected String template;
    protected transient Component data;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction() == getAction()){

                final DownloadTask dt = (DownloadTask)intent.getSerializableExtra(DownloadService.DOWNLOAD_TASK_SERIALIZABLE);
                if(dt != null){
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onDownloadComplete(dt.getUrl(), dt.getSaveDirPath()+dt.getFileName(), dt.getIndex());
                        }
                    });
                }

            }
        }
    };


    protected transient T mView;

    @Override
    public void OnTimer(long timeMs) {

    }

    protected static class FilePath implements Serializable{
        public static final int STATUS_CANPLAY = 0x01;
        public static final int STATUS_NOTPLAY = 0x00;

        public int status = STATUS_NOTPLAY;
        public String url;
    }

    protected List<FilePath> mFileList = new ArrayList<FilePath>();


    public BaseProcessor(Context context, String template){
        mContext = context;
        this.template = template;

        //1920*1080
        mScreen = new XScreen(mContext, WIDTH, HEIGHT);
    }



    public abstract int getType();
    public abstract void updateView(Context context, Component item);
    public abstract T makeView(Context context, Component item);

    public Component getData(){
        return data;
    }

    public  void execute(Context context, Component item){
    }

    public T generateView(Context context, Component item){
        data = item;
        T view = makeView(context, item);
        if(view != null){
            view.setTag(KEY_TAG, this);
        }
        setView(view);
        execute(context, item);


        if(item != null){

            setPosition(context, item.getLeft(), item.getTop(),
                    item.getWidth(), item.getHeight());
        }


        return view;
    }

    protected void setView(T view){
        mView = view;
    }

    public T getView() {
        return mView;
    }

    /**
     *  对模块才起作用
     * @param template
     */
    public void notifyDataSetChange(List<ComponentData>  list, String template){

    }

    protected void addDownloadTask(String url, String index, String action){
//        DownloadEntity entity = new DownloadEntity();
//        entity.setDownloadId(url);
//        entity.setUrl(url);
//        entity.setIndex(index);
//        entity.setDownloadTaskListener(new DownloadListener());
//        entity.setAction(action);
//        DownloadService.startDownloadService(mContext, entity);
        if(!TextUtils.isEmpty(url)){
            String fileName = Md5.MD5(url);
            String suffix = url.substring(url.lastIndexOf("."));
            String filePath = Environment.getExternalStorageDirectory().getPath()+ "/mqclient/" + fileName + "/" + fileName + suffix;
            onDownloadComplete(url, filePath, index);
        }

    }

    protected String getAction(){
        return ""+this.hashCode();
    }

    protected void setPosition(Context context, int x, int y, int width, int height){

        if(mView != null){

//            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
//                    (int)(width * context.getResources().getDisplayMetrics().density),
//                    (int)(height * context.getResources().getDisplayMetrics().density));
//            lp.leftMargin = (int)(x * context.getResources().getDisplayMetrics().density);
//            lp.topMargin = (int)(y * context.getResources().getDisplayMetrics().density);
//            mView.setLayoutParams(lp);

            //TODO: 需要解决误差
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                    (int)mScreen.getWidth(width),
                    (int)mScreen.getHeight(height));
            lp.leftMargin = (int)mScreen.getX(x);
            lp.topMargin = (int)mScreen.getY(y);
            mView.setLayoutParams(lp);
        }

    }

    @Override
    public void onAddView(View parent, View v) {
        Log.d("jacklam", "onAddView:" + this.toString());
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(getAction());
//        mContext.registerReceiver(mReceiver, intentFilter);
        if(isNeedTimer()){
            InactivityTimer.getInstance().addTimerListener(this);
            InactivityTimer.getInstance().start();
        }
        if(isSubscribe()){
            PSubject.getInstance().register(this);
        }

    }

    @Override
    public void onRemoveView(View parent, View v) {
        Log.d("jacklam", "onRemoveView:" + this.toString());
//        if(mReceiver != null){
//            mContext.unregisterReceiver(mReceiver);
//        }
        if(isNeedTimer()) {
            InactivityTimer.getInstance().removeTimerListener(this);
        }
        if(isSubscribe()){
            PSubject.getInstance().deRegister(this);
        }

    }

    private LogRecorder recorder = null;

    protected void enter(String id, String temp){
        Log.d(this.getClass().getName(), "log programe id:" + id);
        Log.d(this.getClass().getName(), "log TerminalGroupItemName:" + temp);
        exit();
        if(this instanceof TemplateProcessor){

            Log.d(this.getClass().getName(), "log enter  TemplateProcessor:" + id);

            recorder =
                    new LogRecorder(id, temp,
                            SharePref.getInstance().getString(SpConstants.WARRANTNO, ""),
                            System.currentTimeMillis(), System.currentTimeMillis(), System.currentTimeMillis());
            Log.d(this.getClass().getName(), "log start:" + id);
            recorder.start();
        }
        else{


        }
    }

    protected void exit(){

        if(this instanceof TemplateProcessor){
            if(recorder != null){
                recorder.end();
            }
        }
        else{

        }
    }

    protected int isMineUrl(String url){

        int  pos = -1;
        if(data != null){

            List<PieceMaterialModel> model = data.getFile();
            if(model != null){
                for(int i = 0; i < model.size(); i++){
                    if(!TextUtils.isEmpty(model.get(i).getUrl()) && model.get(i).getUrl().compareToIgnoreCase(url) == 0){
                        pos = i;
                        break;
                    }
                }
            }

        }
        return pos;
    }

    protected long getTimes(int day, int hour, int minute, int seconds){
        long ms = 0;

        ms = seconds * 1000 +
                minute * (1000*60) +
                hour * (1000*60*60) +
                day * (1000*60*60*24);

        return ms;
    }

    public void Update(final DownloadTask dt){

        if(dt != null && isMineUrl(dt.getUrl()) >= 0 && mContext != null){

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    onDownloadComplete(dt.getUrl(), dt.getSaveDirPath()+dt.getFileName(), dt.getIndex());
                }
            });
        }

    }
    protected void onDownloadComplete(String url, String filePath, String index){

    }

//    private static DownloadTaskListener listener = new DownloadTaskListener() {
//        @Override
//        public void onPrepare(DownloadTask downloadTask){
//
//        }
//
//        @Override
//        public void onStart(DownloadTask downloadTask) {
//
//        }
//
//        @Override
//        public void onDownloading(DownloadTask downloadTask) {
//
//        }
//
//        @Override
//        public void onPause(DownloadTask downloadTask) {
//
//        }
//
//        @Override
//        public void onCancel(DownloadTask downloadTask) {
//
//        }
//
//        @Override
//        public void onCompleted(DownloadTask downloadTask) {
//
//            if(downloadTask != null){
////                Intent intentFilter = new Intent(downloadTask.getAction());
////                intentFilter.putExtra(DownloadService.DOWNLOAD_TASK_SERIALIZABLE, downloadTask);
////                App.getInstance().sendBroadcast(intentFilter);
//                PSubject.getInstance().notify(downloadTask);
//            }
//
//        }
//
//        @Override
//        public void onError(DownloadTask downloadTask, int errorCode) {
//
//        }
//    };

    public void Update(Layer data, List<ComponentData> list, String template){

    }

    public void setBackGround(String url){

    }

    public boolean isSubscribe(){
        return false;
    }

    public boolean isNeedTimer(){
        return false;
    }
}
