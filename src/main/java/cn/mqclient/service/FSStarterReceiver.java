package cn.mqclient.service;

import android.content.Context;
import android.content.Intent;

import cn.mqclient.FullscreenActivity;
import cn.mqclient.LaucherActivity;
import cn.mqclient.Log;
import cn.mqclient.receiver.StarterReceiver;

/**
 * Created by Kanwah on 2016/11/28.
 */

public class FSStarterReceiver extends StarterReceiver {
    @Override
    public void starter(Context context, Intent intent) {
        Log.d("jacklam", "FSStarterReceiver starter");
        //TODO:
        Intent intent1 = new Intent(context, LaucherActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
