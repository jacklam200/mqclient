package cn.mqclient.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.List;

import cn.mqclient.Layer.common.PSubject;
import cn.mqclient.Log;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.provider.Layer;
import cn.mqclient.service.autoTake.CameraService;
import cn.mqclient.utils.ThreadPool;

/**
 * Created by Kanwah on 2016/12/23.
 */

public class PlayReceiver extends BroadcastReceiver {
    public final static String INTENT_PLAY_RECEIVER = "cn.mqclient.receiver.PlayReceiver";
    public final static String JSON_EXTRA = "JSON";
    public final static String LAYER_EXTRA = "LAYER";
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(INTENT_PLAY_RECEIVER)) {
            Log.d(this.getClass().getName(), "read layer Db notify receive");
            final  String json = intent.getStringExtra(JSON_EXTRA);
            final Layer lay = intent.getParcelableExtra(LAYER_EXTRA);
            Log.d(this.getClass().getName(), "start cameraservice");
            CameraService.startProgram(context,lay.getName(), json, lay);
//            ThreadPool.getInstance().submit(new Runnable() {
//                @Override
//                public void run() {
//
//                    if(!TextUtils.isEmpty(json)){
//
//                        List<ComponentData> clist = JSON.parseArray(json, ComponentData.class);
//                        PSubject.getInstance().notify(lay,
//                                clist, lay.getName());
//
//                    }
//                }
//            });

        }
    }
}
