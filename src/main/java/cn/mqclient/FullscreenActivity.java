package cn.mqclient;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import cn.mqclient.Log;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.mqclient.Layer.common.InactivityTimer;
import cn.mqclient.Layer.common.Observer;
import cn.mqclient.Layer.common.PSubject;
import cn.mqclient.adapter.LayerAdapter;
import cn.mqclient.entity.Command;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.entity.Data;
import cn.mqclient.http.download.base.DownloadTask;
import cn.mqclient.provider.Layer;
import cn.mqclient.provider.state.OpStateDao;
import cn.mqclient.provider.state.State;
import cn.mqclient.receiver.MessageReceiver;
import cn.mqclient.service.SubscriberService;
import cn.mqclient.service.TimerService;
import cn.mqclient.utils.ModuleFile;
import cn.mqclient.utils.MqConstants;
import cn.mqclient.utils.SerializableFile;
import cn.mqclient.utils.SharePref;
import cn.mqclient.utils.SpConstants;
import cn.mqclient.utils.ThreadPool;
import cn.mqclient.widget.LayerListView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends BaseActivity implements Observer<Layer, ComponentData>{
    private static final String INTENT_LAUNCH = "launch";
    private static final int WHAT_CHANGE_LAYER = 0x01;
    private static final int WHAT_CHANGE_MODULE = 0x03;
    private static final int WHAT_UNLOCK = 0x02;
    private String moduleId = "";
    private String lockId = "";
    private LayerListView mq_list;

    public static void start(Context context) {
        Intent starter = new Intent(context, FullscreenActivity.class);
        context.startActivity(starter);
    }

    public static void startNewTask(Context context) {
        Intent starter = new Intent(context, FullscreenActivity.class);
        starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(INTENT_LAUNCH, true);
        context.startActivity(starter);
    }

    public void onDownloadPage(View view) {
        DownloadActivity.start(this);
    }

    @Override
    public void Update(DownloadTask task) {

    }

    @Override
    public void Update( final Layer data, final List<ComponentData> list, final String template) {
        Handler handler = new Handler(this.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(FullscreenActivity.this.getClass().getName(), "Update UI");
                mAdapter.notifyDataSetChanged(data, list, template);
            }
        });

    }

    private static class ImgHandler extends Handler{
        private final WeakReference<FullscreenActivity> mView;

        public ImgHandler(FullscreenActivity activity){
            mView = new WeakReference<FullscreenActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == WHAT_CHANGE_MODULE) {
                FullscreenActivity view = mView.get();
                if(view != null){
                    InactivityTimer.getInstance().start();
                    ComponentData data = (ComponentData)msg.obj;
                    view.mList.clear();
                    view.mAdapter.notifyDataSetChanged();
                    view.mList.addAll(data.getData());
                    view.mAdapter.notifyDataSetChanged();
//                    Glide.with(view).load(data.getData()).into(view.fullscreen_image);
                }
            }
//            else if(msg.what == WHAT_CHANGE_LAYER){
//                FullscreenActivity view = mView.get();
//                if(view != null){
//
//                    ComponentData data = (ComponentData)msg.obj;
////                    view.mList.clear();
////                    view.mList.addAll(data.getData());
//                    String template = "";
//                    if(data.getData() != null && data.getData().size() > 0){
//                        template = data.getData().get(0).getTerminalGroupItemName();
//                    }
//                    view.mAdapter.notifyDataSetChanged(data, data.getData(), template);
////                    Glide.with(view).load(data.getData()).into(view.fullscreen_image);
//                }
//            }
            else if(msg.what == WHAT_UNLOCK){

                SharePref.getInstance().clearInfo();
                ThreadPool.getInstance().submit(new Runnable() {
                    @Override
                    public void run() {

                        OpStateDao.getInstance(App.getInstance()).clearAll();

                    }
                });
                FullscreenActivity view = mView.get();
                SubscriberService.stop(view);
                TimerService.stop(view);
                InactivityTimer.getInstance().shutdown();
                LoginActivity.start(view);
                view.finish();
            }
        }
    }

    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private ImageView fullscreen_image;
    private LayerAdapter mAdapter;
    private List<Component> mList = new ArrayList<Component>();

    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    /**
     * 消息处理
     */
    private Handler mHandler = new ImgHandler(this);
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

//    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        PSubject.getInstance().register(this);
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        fullscreen_image = (ImageView)findViewById(R.id.fullscreen_image);
        mq_list = (LayerListView)findViewById(R.id.mq_list);
        mAdapter = new LayerAdapter(this, mList);
        mq_list.setAdapter(mAdapter);
//        Glide.with(this).load("http://img15.3lian.com/2015/f2/50/d/70.jpg").into(fullscreen_image);
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);
        initMQ();
        initTimer();

        if(getIntent() != null && getIntent().getBooleanExtra(INTENT_LAUNCH, false)){
            SerializableFile<Command> sc = new SerializableFile<Command>(Environment.getExternalStorageDirectory().getPath()+ "/mqclient/" + "module.dic");
            Command command = sc.read();
            if(command != null){
                String msg = command.getContent();
                Log.d("FullscreenActivity", "cmd data 2:" + msg);
                ComponentData data = JSON.parseObject(msg, ComponentData.class);
                Message message = mHandler.obtainMessage(WHAT_CHANGE_MODULE, data);
                mHandler.sendMessage(message);

                ThreadPool.getInstance().submit(new Runnable() {
                    @Override
                    public void run() {

                        ModuleFile sc = new ModuleFile(Environment.getExternalStorageDirectory().getPath()+ "/mqclient/" + "layer.dic");
                        List<State> list = sc.read();
                        Log.d("jacklam", " state  size:" + list.size());
                        if(list != null){
                            for(int i = 0; i < list.size(); i++){
                                State state = list.get(i);
                                final Layer data = JSON.parseObject(state.getJson(), Layer.class);
                                mHandler.postAtTime(new Runnable() {
                                    @Override
                                    public void run() {

                                        List<ComponentData> clist = JSON.parseArray(data.getJson(), ComponentData.class);

                                        if(data != null)
                                            PSubject.getInstance().notify(data, clist, data.getName());
                                    }
                                }, 1000);

                            }

                        }

                    }
                });

            }

        }
    }

    private void initTimer(){
        Intent intent = new Intent(this, TimerService.class);
        startService(intent);
    }

    public void setBackGroud(String url, String tempate){
        if(mAdapter != null){
            mAdapter.setBackGround(url, tempate);
        }
//        Glide.with(this).load(url).into(fullscreen_image);
    }

    private void initMQ() {
        filter = new IntentFilter(MessageReceiver.ACTION_NEW_MESSAGE);
        receiver = new MessageReceiver<Command>() {

            @Override
            public void onMessageReceived(String message) {
                Log.d("FullScreenActivity", "message:" + message);

            }

            @Override
            public void onMessageReceived(Command command) {

                if(command != null){

                    if(command.getCmd() != null && command.getCmd().compareToIgnoreCase(MqConstants.CMD_LOCK) == 0){
                        // 相同id不执行
                        if(lockId.compareToIgnoreCase(command.getId()) == 0)
                            return;

                        lockId = command.getId();
                        String msg = command.getContent();
                        Log.d("FullscreenActivity", "cmd data:" + msg);
                        String module = Environment.getExternalStorageDirectory().getPath()+ "/mqclient/" + "module.dic";
                        File file = new File(module);
                        if(file.exists())
                            file.delete();

                        OpStateDao.getInstance(App.getInstance()).clearAll();
                        Message message = mHandler.obtainMessage(WHAT_UNLOCK, null);
                        mHandler.sendMessage(message);
                    }
                    else if(command.getCmd() != null && command.getCmd().compareToIgnoreCase(MqConstants.CMD_SPLIT) == 0){
                        SerializableFile<Command> sc = new SerializableFile<Command>(Environment.getExternalStorageDirectory().getPath()+ "/mqclient/" + "module.dic");
                        sc.write(command);
                        String msg = command.getContent();
                        Log.d("FullscreenActivity", "cmd data:" + msg);
                        if(TextUtils.isEmpty(moduleId) ||
                                moduleId.compareToIgnoreCase(command.getId()) != 0){
                            ComponentData data = JSON.parseObject(msg, ComponentData.class);
                            Message message = mHandler.obtainMessage(WHAT_CHANGE_MODULE, data);
                            mHandler.sendMessage(message);
                            moduleId = command.getId();
                        }

                    }



                }
            }
        };

        registerReceiver(receiver, filter);
//        startService(new Intent(getApplicationContext(), SubscriberService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(1);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            moveTaskToBack(false);
            Intent home = new Intent(Intent.ACTION_MAIN);
//            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mList != null)
            mList.clear();
        if(mAdapter != null)
            mAdapter.notifyDataSetChanged();
        InactivityTimer.getInstance().shutdown();
//        SubscriberService.stop(this);
        unregisterReceiver(receiver);
//        destroyTimer();
        PSubject.getInstance().deRegister(this);
    }

    private void destroyTimer(){
        Intent intent = new Intent(this, TimerService.class);
        stopService(intent);
    }

}
