package cn.mqclient.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import cn.mqclient.App;
import cn.mqclient.Layer.common.DownloadListener;
import cn.mqclient.Log;
import cn.mqclient.entity.Command;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.ComponentArray;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.entity.PieceMaterialModel;
import cn.mqclient.http.download.base.DownloadEntity;
import cn.mqclient.http.download.base.DownloadManager;
import cn.mqclient.http.download.base.DownloadStatus;
import cn.mqclient.http.download.base.DownloadTask;
import cn.mqclient.http.download.base.DownloadTaskListener;
import cn.mqclient.rabbitpusher.RabbitPublishService;

/**
 * Created by LinZaixiong on 2016/12/19.
 */

public class DownloadLayer implements DownloadTaskListener {

    private ComponentArray mLayer;
    private Command cmd;
    private List<PieceMaterialModel> mList = new ArrayList<>();
    private int mCompleteSize = 0;
    private LayerParser parser = new LayerParser();

    public DownloadLayer(ComponentArray layer, Command cmd){
        mLayer = layer;
        this.cmd = cmd;
    }

    public void download(){

        if(mLayer != null) {

            parser.prepare(mLayer, cmd);

            mCompleteSize = 0;
            List<ComponentData> data = mLayer.getData().getProgrammes();

            if (data != null && data.size() > 0) {

                resCollect(data);

                if(mList.size() > 0){
                    for(int i = 0; i < data.size(); i++){
                        data.get(i).setId(cmd.getId());
                        findResToDownload(data.get(i).getProgrammeItems());
                    }
                }
                else{
                    dispatchLayer(mLayer, DownloadStatus.DOWNLOAD_STATUS_COMPLETED, "100%");
                }


            }
        }
    }

    private void dispatchLayer( ComponentArray data, int status, String down_percent){
        parser.execute(data, cmd, status, down_percent);
    }

    private void resCollect( List<ComponentData> data){

        mList.clear();

        for(int i = 0; i < data.size(); i++){
            List<Component> programmeItems = data.get(i).getProgrammeItems();

            for(int j = 0; j < programmeItems.size(); j++){
                List<PieceMaterialModel> list = programmeItems.get(j).getFile();
                if(list != null)
                    mList.addAll(list);
            }
        }
    }

    private void findResToDownload(List<Component> programmeItems) {

        if(programmeItems != null){

            for(int i = 0; i < programmeItems.size(); i++){

                List<PieceMaterialModel> list = programmeItems.get(i).getFile();

                if(list != null){
                    for(int j = 0; j < list.size(); j++){
                        startDownload(list.get(j));
                    }
                }

            }
        }
    }

    private void startDownload(PieceMaterialModel model){

        if(!TextUtils.isEmpty(model.getUrl())){

            DownloadEntity entity = new DownloadEntity();
            entity.setUrl(model.getUrl());
            entity.setDownloadId(Md5.MD5(model.getUrl()+model.getPieceId()+model.getId()));
            entity.setIndex(model.getMaterialId());
            entity.setAction("download");
            DownloadTask task = generateDownloadTask(entity);
            Log.d(this.getClass().getName(), "startDownload id:" + task.getId());
            Log.d(this.getClass().getName(), "startDownload:" + task.getUrl());
            DownloadManager.getInstance(App.getInstance()).addDownloadTask(task, this);

        }
        else{
            onCompleted(null);
        }
    }

    private boolean isDownloaded(String url){

        boolean isRet = false;
        isRet = DownloadManager.getInstance(App.getInstance()).isDownloaded(url);
        return isRet;
    }
    private DownloadTask generateDownloadTask(DownloadEntity entity){

        DownloadTask task = new DownloadTask();
        String fileName = Md5.MD5(entity.getUrl());
        String suffix = entity.getUrl().substring(entity.getUrl().lastIndexOf("."));
        task.setFileName(fileName + suffix);
        task.setIndex(entity.getIndex());
        task.setId(entity.getDownloadId());
        task.setAction(entity.getAction());
        task.setSaveDirPath(Environment.getExternalStorageDirectory().getPath()+
                "/mqclient/" + fileName + "/");
        task.setUrl(entity.getUrl());

        return task;
    }


    @Override
    public void onPrepare(DownloadTask downloadTask) {
        Log.d(this.getClass().getName(), "onPrepare:" + (downloadTask != null ? downloadTask.getUrl() : ""));
    }

    @Override
    public void onStart(DownloadTask downloadTask) {
        Log.d(this.getClass().getName(), "onStart:" + (downloadTask != null ? downloadTask.getUrl() : ""));
    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {
//        Log.d(this.getClass().getName(), "onDownloading:" + (downloadTask != null ? downloadTask.getUrl() : ""));
    }

    @Override
    public void onPause(DownloadTask downloadTask) {
        Log.d(this.getClass().getName(), "onPause:" + (downloadTask != null ? downloadTask.getUrl() : ""));
    }

    @Override
    public void onCancel(DownloadTask downloadTask) {
        Log.d(this.getClass().getName(), "onCancel:" + (downloadTask != null ? downloadTask.getUrl() : ""));
    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        Log.d(this.getClass().getName(), "onCompleted id:" + downloadTask.getId());
        Log.d(this.getClass().getName(), "onCompleted:" + (downloadTask != null ? downloadTask.getUrl() : ""));
        mCompleteSize++;
        if(mCompleteSize == mList.size()){
            Log.d(this.getClass().getName(), "download all done");
            sendDoneMsg(cmd, "100%");
            dispatchLayer(mLayer ,DownloadStatus.DOWNLOAD_STATUS_COMPLETED, "100%");
        }
        else{

            int percent = 100;
            if(mList.size() != 0)
                percent = mCompleteSize / mList.size() * 100;
            sendDoneMsg(cmd, percent + "%");
            dispatchLayer(mLayer ,DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING, percent + "%");
        }
    }

    @Override
    public void onError(DownloadTask downloadTask, int i) {
        int retry = 0;
        Log.d(this.getClass().getName(), "onError id:" + downloadTask.getId());
        Log.d(this.getClass().getName(), "onError:" + downloadTask.getUrl());
        if(downloadTask != null) {

            retry = downloadTask.getRetryTime();
            Log.d(this.getClass().getName(), "onError:" + downloadTask.getUrl() + " times:" + retry);
            if (retry <= 3){
                DownloadManager.getInstance(App.getInstance()).addDownloadTask(downloadTask, this);
            }
            else{
                sendUnDoneMsg(cmd, downloadTask.getUrl());
            }
        }

    }

    private void sendDoneMsg(Command cmd, String percent){
        if(cmd != null && !TextUtils.isEmpty(cmd.getServerName())){
            cmd.setState(Command.CMD_DONE);
            cmd.setData(""+percent);
            String msg =  JSON.toJSONString(cmd);
            RabbitPublishService service = new RabbitPublishService(cmd.getServerName());
            service.send(msg);
        }
    }

    private void sendUnDoneMsg(Command cmd, String msg1){
        if(cmd != null && !TextUtils.isEmpty(cmd.getServerName())){
            cmd.setState(Command.CMD_UNDONE);
            cmd.setData("download failed:" + msg1);
            String msg =  JSON.toJSONString(cmd);
            RabbitPublishService service = new RabbitPublishService(cmd.getServerName());
            service.send(msg);
        }
    }
}
