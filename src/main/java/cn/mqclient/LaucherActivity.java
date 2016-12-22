package cn.mqclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import cn.mqclient.entity.http.MQConfigEntity;
import cn.mqclient.service.SubscriberService;
import cn.mqclient.utils.SharePref;
import cn.mqclient.utils.SpConstants;

/**
 * Created by LinZaixiong on 2016/10/22.
 */

public class LaucherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_launcher);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MQConfigEntity.MQConfig config = new MQConfigEntity.MQConfig();
                String warrantNo = SharePref.getInstance().getString(SpConstants.WARRANTNO, "");
                config.getInfo();
                if(config.isSuccess() && !TextUtils.isEmpty(warrantNo)){
                    SubscriberService.start(App.getInstance(), config);
                    FullscreenActivity.startNewTask(LaucherActivity.this);
                }
                else{
                    LoginActivity.start(LaucherActivity.this);
                }
                finish();
            }
        },1000 * 2);
    }
}
