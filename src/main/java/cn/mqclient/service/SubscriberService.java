package cn.mqclient.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.os.Environment;
import android.os.Message;
import android.provider.SyncStateContract;
import android.system.Os;
import android.text.TextUtils;
import cn.mqclient.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.hpl.sparta.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cn.mqclient.App;
import cn.mqclient.Config;
import cn.mqclient.Layer.common.DownloadListener;
import cn.mqclient.Layer.common.PSubject;
import cn.mqclient.entity.Command;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.ComponentArray;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.entity.PieceMaterialModel;
import cn.mqclient.entity.http.MQConfigEntity;
import cn.mqclient.http.download.base.DownloadEntity;
import cn.mqclient.provider.Layer;
import cn.mqclient.provider.OpLayerDao;
import cn.mqclient.rabbitpusher.io.MessageStream;
import cn.mqclient.rabbitpusher.services.RabbitReaderService;
import cn.mqclient.receiver.MessageReceiver;
import cn.mqclient.service.autoInstall.InstallController;
import cn.mqclient.service.autoTake.CameraService;
import cn.mqclient.utils.AppUtils;
import cn.mqclient.utils.DateTimeUtil;
import cn.mqclient.utils.DownloadLayer;
import cn.mqclient.utils.ModuleFile;
import cn.mqclient.utils.MqConstants;
import cn.mqclient.utils.OsUtils;
import cn.mqclient.utils.SerializableFile;
import cn.mqclient.utils.UpdateApk;


/**
 * Created by LinZaixiong on 2016/9/7.
 */
public class SubscriberService extends RabbitReaderService {

    public static void start(Context context, MQConfigEntity.MQConfig entity) {
        stop(context);
        Intent starter = new Intent(context, SubscriberService.class);
        starter.putExtra(INTENT_CONFIG, entity);
        context.startService(starter);
    }

    public static void stop(Context context) {
        Log.d("jacklam", " stop push message");
        Intent starter = new Intent(context, SubscriberService.class);
        context.stopService(starter);
    }


    @Override
    public void onConnectionSuccessful() {
        Toast.makeText(getApplicationContext(), "connection successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionLost() {
        Toast.makeText(getApplicationContext(), "connection successful", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageReceived(Command cmd, String message) {

       if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.CMD_PROGRAM) == 0){
            String msg = cmd.getContent();
            Log.d("FullscreenActivity", "cmd data:" + msg);
           ComponentArray dataList = JSON.parseObject(msg, ComponentArray.class);
           //TODO:看是否开启线程
           DownloadLayer layer = new DownloadLayer(dataList, cmd);
           layer.download();
//            Message message = mHandler.obtainMessage(WHAT_CHANGE_LAYER, data);
//            mHandler.sendMessage(message);
        }
        else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.CMD_REBOOT) == 0){
           OsUtils.rebootNow();
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.PHOTOGRAPH) == 0){
           CameraService.start(this, cmd.getId());
       }
        else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.SCREENSHOT) == 0){
            //TODO:startRecord(Context context)
           CameraService.startRecord(this, cmd.getId());
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.TIMED_SHUTDOWN) == 0){
           String msg = cmd.getContent();
           Command data = (Command)JSON.parseObject(msg, Command.class);
//           msg = (String)data.getData();
           Log.d("FullscreenActivity", "TIMED_SHUTDOWN cmd data:" + msg);
           Long times = (Long)data.getData();

           String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(new Date(times));
           String day = date.substring(0, 10);
           String time = date.substring(11, 19);
           if(times != null)
                OsUtils.setPowerOff( day, time);
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.TIMING_BOOT) == 0){
           String msg = cmd.getContent();
           Command data = (Command)JSON.parseObject(msg, Command.class);
//           msg = (Long)data.getData();
           Log.d("FullscreenActivity", "TIMED_SHUTDOWN cmd data:" + msg);
           Long times = (Long)data.getData();

           String date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.CHINA).format(new Date(times));
           String day = date.substring(0, 10);
           String time = date.substring(11, 19);

           if(times != null)
               OsUtils.setPowerOn( day, time);
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.UPGRADE) == 0){

           String msg = cmd.getContent();
           Command data = (Command)JSON.parseObject(msg, Command.class);
           msg = (String)data.getData();
           if(!TextUtils.isEmpty(msg)){

               UpdateApk.update(msg,
                                   Environment.getExternalStorageDirectory().getPath() + "/mqclient/update.apk",
                                   new UpdateApk.OnUpdateComplete() {
                                       @Override
                                       public void onComplete(int code, String path) {
                                            if(code == 0){

                                                try {
                                                    Log.d("jacklam", "update apk:install" );
                                                    int version = UpdateApk.getVersionNameFromApk(App.getInstance(),path);
                                                    int vCode = AppUtils.getAppVersionCode(App.getInstance());
                                                    if(version > vCode)
                                                        mInstaller.install(path);
                                                    else{
                                                        Log.d("jacklam", "update apk: version error");
                                                    }
                                                }
                                                catch (Exception e){
                                                    Log.d("jacklam", "update apk:" + e.getMessage());
                                                    e.printStackTrace();
                                                }

                                            }
                                       }
                                   });
           }
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.VOLUME) == 0){

           String msg = cmd.getContent();
           Command data = (Command)JSON.parseObject(msg, Command.class);
//           msg = (String)data.getData();
           Log.d("FullscreenActivity", "TIMED_SHUTDOWN cmd data:" + msg);
           Integer times = (Integer)data.getData();

           if(times != null){
               OsUtils.setSystemVolume(App.getInstance(), times/10);
           }
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.STOP) == 0){
           SerializableFile<Command> sc = new SerializableFile<Command>(Environment.getExternalStorageDirectory().getPath()+ "/mqclient/" + "module.dic");
           Command command = sc.read();
           if(command != null) {
               String msg = command.getContent();
               Log.d("FullscreenActivity", "cmd data 2:" + msg);
               cmd.setCmd(MqConstants.CMD_SPLIT);
               cmd.setContent(msg);
           }
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.STATE) == 0){

       }
        sendBroadcast(MessageReceiver.getBroadcastIntent(cmd, getApplicationContext()));
//        sendBroadcast(MessageReceiver.getBroadcastIntent(message, getApplicationContext()));

//        if(stream != null){
//            Command command = JSON.parseObject(message, Command.class);
//            command.setState(100);
//            message = JSON.toJSONString(command);
//            try {
//                stream.write(message);
//                Log.d("SubscriberService", "SubscriberService write:" + message);
//            }
//            catch (Exception e){
//                Log.d("SubscriberService", "SubscriberService:" + e.getMessage());
//                e.printStackTrace();
//            }
//
//        }
    }

//    private Layer invert(ComponentData data, String msg){
//        Layer layer = new Layer();
//        layer.setId(data.getId());
//        layer.setBegin_time(new Date(data.getBroadcastStartTime()));
//        layer.setEnd_time(new Date(data.getBroadcastEndTime()));
//        layer.setGroup_name(data.getId());
//        layer.setJson(msg);
//        layer.setName(data.getTemplate());
//        return layer;
//    }
//
//    private Layer invert2(long start, long end, ComponentData data, String msg){
//        Layer layer = new Layer();
//        layer.setId(data.getId() + UUID.randomUUID());
//        layer.setBegin_time(new Date(start));
//        layer.setEnd_time(new Date(end));
//        layer.setGroup_name(data.getId());
//        layer.setJson(msg);
//        layer.setName(data.getTemplate());
//        return layer;
//    }
//
//    private void findResToDownload(List<Component> data){
//
//        if(data != null){
//            for(int i = 0; i < data.size(); i++){
//
//                List<PieceMaterialModel> list = data.get(i).getFile();
//                if(list != null){
//                    for(int j = 0; j < list.size(); j++){
//                        if(!TextUtils.isEmpty(list.get(j).getUrl()))
//                            addDownloadTask(list.get(j).getUrl(), data.get(i).getId(), "download");
//                    }
//                }
//
//            }
//        }
//
//    }
//
//    private void addDownloadTask(String url, String id, String action){
//
//        DownloadEntity entity = new DownloadEntity();
//        entity.setUrl(url);
//        entity.setIndex(id);
//        entity.setDownloadTaskListener(new DownloadListener());
//        entity.setAction(action);
//        DownloadService.startDownloadService(getApplicationContext(), entity);
//    }

    @Override
    public String getChannelName() {
        return Config.CLIENT_NAME;
    }

    @Override
    public String getServerChannelName() {
        return Config.SERVER_NAME;
    }

    @Override
    public String getHostName() {
        return Config.HOST_NAME;
    }

    @Override
    public String getUserName() {
        return Config.USER_NAME;
    }

    @Override
    public String getUserPsw() {
        return Config.USER_PSW;
    }

    @Override
    public boolean running() {
        return true;
    }
}
