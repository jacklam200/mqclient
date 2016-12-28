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
import cn.mqclient.rabbitpusher.RabbitPublishService;
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

           CMD_PROGRAM(cmd);

        }
        else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.CMD_REBOOT) == 0){
           reboot();
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.PHOTOGRAPH) == 0){
           PHOTOGRAPH(cmd);

       }
        else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.SCREENSHOT) == 0){
           SCREENSHOT(cmd);
          
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.TIMED_SHUTDOWN) == 0){
           timingShutDown(cmd);
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.TIMING_BOOT) == 0){
           timingBoot(cmd);
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.UPGRADE) == 0){

          UPGRADE(cmd);
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.VOLUME) == 0){

           VOLUME(cmd);
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.STOP) == 0){
           STOP(cmd);
       }
       else if(cmd != null && cmd.getCmd() != null && cmd.getCmd().compareToIgnoreCase(MqConstants.STATE) == 0){
           STATE(cmd);

       }
        sendBroadcast(MessageReceiver.getBroadcastIntent(cmd, getApplicationContext()));

    }

    private void sendDoneMsg(Command cmd){
        if(cmd != null && !TextUtils.isEmpty(cmd.getServerName())){
            cmd.setState(Command.CMD_DONE);
            String msg =  JSON.toJSONString(cmd);
            RabbitPublishService service = new RabbitPublishService(cmd.getServerName());
            service.send(msg);
        }
    }

    private void sendUnDoneMsg(Command cmd){
        if(cmd != null && !TextUtils.isEmpty(cmd.getServerName())){
            cmd.setState(Command.CMD_UNDONE);
            String msg =  JSON.toJSONString(cmd);
            RabbitPublishService service = new RabbitPublishService(cmd.getServerName());
            service.send(msg);
        }
    }

    private void STATE(Command cmd) {

        if(cmd != null){
            cmd.setState(100);
            String msg =  JSON.toJSONString(cmd);
            RabbitPublishService service = new RabbitPublishService(cmd.getServerName());
            service.send(msg);
        }

    }

    private void STOP(Command cmd) {
        SerializableFile<Command> sc = new SerializableFile<Command>(Environment.getExternalStorageDirectory().getPath()+ "/mqclient/" + "module.dic");
        Command command = sc.read();
        if(command != null) {
            String msg = command.getContent();
            Log.d("FullscreenActivity", "cmd data 2:" + msg);
            cmd.setCmd(MqConstants.CMD_SPLIT);
            cmd.setContent(msg);
            sendDoneMsg(cmd);
        }

    }

    private void VOLUME(Command cmd) {
        String msg = cmd.getContent();
        Command data = (Command)JSON.parseObject(msg, Command.class);
//           msg = (String)data.getData();
        Log.d("FullscreenActivity", "TIMED_SHUTDOWN cmd data:" + msg);
        Integer times = (Integer)data.getData();

        if(times != null){
            OsUtils.setSystemVolume(App.getInstance(), times/10);
            sendDoneMsg(cmd);
        }

    }

    private void UPGRADE(final Command cmd) {
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
                                    if(version > vCode){
                                        mInstaller.install(path);
                                        sendDoneMsg(cmd);
                                    }


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

    private void CMD_PROGRAM(Command cmd) {
        String msg = cmd.getContent();
        Log.d("FullscreenActivity", "cmd data:" + msg);
        ComponentArray dataList = JSON.parseObject(msg, ComponentArray.class);
        //TODO:看是否开启线程
        DownloadLayer layer = new DownloadLayer(dataList, cmd);
        layer.download();
//            Message message = mHandler.obtainMessage(WHAT_CHANGE_LAYER, data);
//            mHandler.sendMessage(message);
    }

    private void SCREENSHOT(Command cmd) {
        CameraService.startRecord(this, cmd.getId());
    }

    private void reboot(){
        OsUtils.rebootNow();

    }

    private void timingBoot(Command cmd){
        String msg = cmd.getContent();
        Command data = (Command)JSON.parseObject(msg, Command.class);
//           msg = (Long)data.getData();
        Log.d("FullscreenActivity", "TIMED_SHUTDOWN cmd data:" + msg);
        Long times = (Long)data.getData();

        String date = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA).format(new Date(times));
        String day = date.substring(0, 10);
        String time = date.substring(11, 16);

        if(times != null)
            OsUtils.setPowerOn( day, time);
    }


    private void timingShutDown(Command cmd){

        String msg = cmd.getContent();
        Command data = (Command)JSON.parseObject(msg, Command.class);
//           msg = (String)data.getData();
        Log.d("FullscreenActivity", "TIMED_SHUTDOWN cmd data:" + msg);
        Long times = (Long)data.getData();

        String date = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA).format(new Date(times));
        String day = date.substring(0, 10);
        String time = date.substring(11, 16);
        if(times != null)
            OsUtils.setPowerOff( day, time);
    }

    private void PHOTOGRAPH(Command cmd){
        CameraService.start(this, cmd.getId());
    }



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
