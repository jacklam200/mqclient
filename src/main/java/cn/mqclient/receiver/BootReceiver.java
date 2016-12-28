package cn.mqclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import cn.mqclient.App;
import cn.mqclient.FullscreenActivity;
import cn.mqclient.LaucherActivity;
import cn.mqclient.entity.http.MQConfigEntity;
import cn.mqclient.service.SubscriberService;

/**
 * Created by Kanwah on 2016/11/29.
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(BOOT_COMPLETED)) {
            Intent intent1 = new Intent(context, LaucherActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }



    }
}
