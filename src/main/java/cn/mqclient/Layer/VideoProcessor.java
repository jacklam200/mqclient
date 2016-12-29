package cn.mqclient.Layer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import cn.mqclient.Log;
import android.view.View;
import android.widget.VideoView;

import java.util.List;

import cn.mqclient.entity.Component;
import cn.mqclient.entity.PieceMaterialModel;
import cn.mqclient.http.download.base.DownloadTask;
import cn.mqclient.widget.CustomVideoView;
import cn.mqclient.widget.media.IRenderView;
import cn.mqclient.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by LinZaixiong on 2016/9/9.
 */
public class VideoProcessor extends BaseProcessor<IjkVideoView> implements IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnPreparedListener {
    public final static String TYPE_STR = "video";
    public final static int TYPE_INT = 3;



    private String path = "";
    private int index = -1;
    private List<PieceMaterialModel> paths;
    private boolean isPlaying;

    public VideoProcessor(Context context, String template) {
        super(context, template);
    }

    @Override
    public int getType() {
        return TYPE_INT;
    }

    @Override
    public IjkVideoView makeView(Context context, Component item) {
        IjkVideoView view = new IjkVideoView(context);
        view.setBackgroundColor(0x00ffffff);
        view.setOnCompletionListener(this);
        view.setOnPreparedListener(this);
        view.setOnErrorListener(this);
        return view;
    }

    @Override
    public void updateView(Context context, Component item) {
//        if(item != null && item.getFile() != null &&
//                item.getFile().length > 0 && mView != null){
//            paths = item.getFile();
//            playVideo(index, paths);
//
//        }
    }

    private void playVideo(int idx, String fielPath){
        Log.d("jacklam", "video playVideo:" +fielPath);
        if(!isPlaying){
            if(idx == -1){

                if(index+1 > paths.size()){
                    idx  = 0;
                }
                else{
                    idx = index + 1;
                }
            }
            if(true/*path.compareToIgnoreCase(fielPath) != 0*/){
                mView.setVideoPath(fielPath);
                isPlaying = true;
                Log.d("jacklam", "video playVideo start:" +fielPath);
                mView.start();
                path = fielPath;
                index = idx;
                if(paths != null && index >= paths.size()){
                    index = 0;
                }
            }
        }


    }

    private void stopVideo(){
        if(mView != null){
            mView.stopPlayback();
        }
    }

    private void playVideo(int idx, List<PieceMaterialModel> paths){
        Log.d("jacklam", "video playVideo");
//        if(path.compareToIgnoreCase(paths.get(index).getUrl()) != 0){
//            mView.setVideoPath(paths.get(index).getUrl());
//            mView.start();
//            path = paths.get(index).getUrl();
//            index++;
//            if(index >= paths.size()){
//                index = 0;
//            }
//        }
        if(idx >= paths.size()){
            idx = 0;
        }
        Log.d("jacklam", "video playVideo idx:" + idx);
        if(true/*path.compareToIgnoreCase(paths.get(idx).getUrl()) != 0*/){
            Log.d("jacklam", "video playVideo addDownloadTask:" + paths.get(idx).getUrl());
            addDownloadTask(paths.get(idx).getUrl(), paths.get(idx).getPieceId(), paths.get(idx).getId());
        }

    }

    @Override
    public void execute(Context context, Component item) {

        if(item != null && item.getFile() != null && item.getFile().size()> 0){
            paths = item.getFile();
            for(int i = 0; i  < item.getFile().size(); i++){
                FilePath file = new FilePath();
                file.status = FilePath.STATUS_NOTPLAY;
                file.url = item.getFile().get(i).getUrl();
                mFileList.add(file);
                addDownloadTask(item.getFile().get(i).getUrl(), item.getFile().get(i).getPieceId(), item.getFile().get(i).getId());
            }

        }
    }


    @Override
    public void onAddView(View parent, View v) {
        super.onAddView(parent, v);
    }

    @Override
    public void onRemoveView(View parent, View v) {
        super.onRemoveView(parent, v);
        if(v == mView){
//            mView.quit();
            stopVideo();
        }
    }

    @Override
    protected void onDownloadComplete(String url, String filePath, String id) {
        super.onDownloadComplete(url, filePath, id);
        Log.d("jacklam", "video onDownloadComplete url :" + url);
        Log.d("jacklam", "video onDownloadComplete filePath :" + filePath);
        int pos = isMineUrl(url);
        Log.d("jacklam", "video onDownloadComplete isMineUrl:" +pos);
        if(pos >= 0 /*&& data.getId().compareToIgnoreCase(id) == 0*/){
            if(!mView.isPlaying())
                playVideo(pos, filePath);
        }

    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
//        playVideo(index, paths);
        Log.d("jacklam", "video onCompletion");
        isPlaying = false;
        playVideo(index+1, paths);
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
//        playVideo(index, paths);
        isPlaying = false;
        playVideo(index+1, paths);
        return true;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {

    }

    public boolean isSubscribe(){
        return true;
    }

    @Override
    public boolean isNeedTimer(){
        return false;
    }
}
