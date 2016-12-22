package cn.mqclient.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import cn.mqclient.App;
import cn.mqclient.Layer.common.DownloadListener;
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
                List<PieceMaterialModel> list = programmeItems.get(i).getFile();
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
                        startDownload(list.get(i));
                    }
                }

            }
        }
    }

    private void startDownload(PieceMaterialModel model){

        if(!TextUtils.isEmpty(model.getUrl())){

            DownloadEntity entity = new DownloadEntity();
            entity.setUrl(model.getUrl());
            entity.setIndex(model.getMaterialId());
            entity.setAction("download");
            DownloadTask task = generateDownloadTask(entity);
            DownloadManager.getInstance(App.getInstance()).addDownloadTask(task, this);
        }
    }

    private DownloadTask generateDownloadTask(DownloadEntity entity){

        DownloadTask task = new DownloadTask();
        String fileName = Md5.MD5(entity.getUrl());
        String suffix = entity.getUrl().substring(entity.getUrl().lastIndexOf("."));
        task.setFileName(fileName + suffix);
        task.setIndex(entity.getIndex());
        task.setId(entity.getUrl());
        task.setAction(entity.getAction());
        task.setSaveDirPath(Environment.getExternalStorageDirectory().getPath()+
                "/mqclient/" + fileName + "/");
        task.setUrl(entity.getUrl());

        return task;
    }


    @Override
    public void onPrepare(DownloadTask downloadTask) {

    }

    @Override
    public void onStart(DownloadTask downloadTask) {

    }

    @Override
    public void onDownloading(DownloadTask downloadTask) {

    }

    @Override
    public void onPause(DownloadTask downloadTask) {

    }

    @Override
    public void onCancel(DownloadTask downloadTask) {

    }

    @Override
    public void onCompleted(DownloadTask downloadTask) {
        mCompleteSize++;
        if(mCompleteSize == mList.size()){
            dispatchLayer(mLayer ,DownloadStatus.DOWNLOAD_STATUS_COMPLETED, "100%");
        }
        else{

            int percent = 100;
            if(mList.size() != 0)
                percent = mCompleteSize / mList.size() * 100;
            dispatchLayer(mLayer ,DownloadStatus.DOWNLOAD_STATUS_DOWNLOADING, percent + "%");
        }
    }

    @Override
    public void onError(DownloadTask downloadTask, int i) {
        int retry = 0;
        if(downloadTask != null) {

            retry = downloadTask.getRetryTime();
            if (retry <= 3){
                DownloadManager.getInstance(App.getInstance()).addDownloadTask(downloadTask, this);
            }
        }

    }
}
