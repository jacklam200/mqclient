package cn.mqclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.umeng.analytics.MobclickAgent;

import java.io.File;

import cn.mqclient.service.autoInstall.InstallController;

/**
 * Created by LinZaixiong on 2016/10/22.
 */

public abstract class BaseActivity extends AppCompatActivity {
//    private InstallController installController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        App.IS_IN = true;
        super.onCreate(savedInstanceState);
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/mqclient");
        if(!file.exists())
            file.mkdirs();

//        installController = new InstallController();
//        installController.bind(this);
        //TODO:needRegister receiver
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
//
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏

    }

    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
