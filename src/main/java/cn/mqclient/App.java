package cn.mqclient;

import android.app.Application;
import android.os.Environment;

import com.umeng.analytics.MobclickAgent;

import java.util.concurrent.TimeUnit;

import cn.mqclient.service.ServiceManager;
import cn.mqclient.third.location.THLocationManager;
import cn.mqclient.utils.SharePref;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by LinZaixiong on 2016/9/7.
 */
public class App extends BaseApplication {

    private static App mInstance = null;
    public static boolean IS_IN = false;
    public static ServiceManager mServiceManager = null;
    private static String rootPath = "/mymusic";
    private String latitude = "-1"; // 维
    private String longitude = "-1";// 经
    private boolean isFront;

    public static App getInstance(){
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        mInstance = this;
    }

    public boolean isToFront(){
        return isFront;
    }

    public void setToFront(boolean isFront){
        this.isFront = isFront;
    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    private void init(){
        if(mServiceManager == null){
            mServiceManager = new ServiceManager(this);
        }

        THLocationManager.getInstance().register();
        THLocationManager.getInstance().start();

        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);

    }

    @Override
    public void saveLocatedInfo(double v, double v1, String s, String s1) {
        latitude = v1 + "";
        longitude = v + "";

        Log.d("jacklam", "latitude:" + latitude);
        Log.d("jacklam", "longitude:" + longitude);
        SharePref.getInstance().setValue("latitude",""+ latitude);
        SharePref.getInstance().setValue("longitude", ""+ longitude);
        super.saveLocatedInfo(v, v1, s, s1);

        THLocationManager.getInstance().stop();
        THLocationManager.getInstance().deregister();

    }
}
