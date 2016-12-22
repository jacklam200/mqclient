package cn.mqclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.mqclient.App;
import cn.mqclient.FullscreenActivity;
import cn.mqclient.entity.http.MQConfigEntity;
import cn.mqclient.service.SubscriberService;

/**
 * Created by Kanwah on 2016/11/29.
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MQConfigEntity.MQConfig config = new MQConfigEntity.MQConfig();
        config.getInfo();
        if(config.isSuccess()){
            SubscriberService.start(App.getInstance(), config);
            FullscreenActivity.startNewTask(context);
        }

    }
}
