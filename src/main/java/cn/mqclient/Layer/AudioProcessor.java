package cn.mqclient.Layer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import cn.mqclient.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import cn.mqclient.async.ExecutorDelivery;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.MusicInfo;
import cn.mqclient.entity.PieceMaterialModel;
import cn.mqclient.service.MediaService;
import cn.mqclient.service.MusicConstants;
import cn.mqclient.service.MusicUtils;
import cn.mqclient.service.aidl.IMediaService;

/**
 * Created by LinZaixiong on 2016/9/28.
 */

public class AudioProcessor extends BaseProcessor<View> implements MusicConstants{
    public final static String TYPE_STR = "audio";
    public final static int TYPE_INT = 6;
    private IMediaService mService;
    private List<MusicInfo> mMusicList = new ArrayList<MusicInfo>();
    public int mPlayingSongPosition;
    private int mCurMusicId;
    private ServiceConnection mServiceConnection;
    private int mCurMode;
    private List<PieceMaterialModel> songs;
    boolean isInit = false;
    private int mIndex = 0;
    private Component mItem;

    public AudioProcessor(Context context, String template){
        super(context, template);

    }

    @Override
    public int getType() {
        return TYPE_INT;
    }

    @Override
    public void updateView(Context context, Component item) {

    }

    @Override
    public void execute(Context context, Component item) {
        super.execute(context, item);
        mItem = item;
        initConnection();

//        initSongs(songs);

    }

    private void initSongs(String songs, int pos){

        if(mService != null && songs != null){

            MusicInfo info = new MusicInfo();
            info.data = songs;
            info.songId = pos;
            mMusicList.add(info);
            try {
                mService.refreshMusicList(mMusicList);
                if(mMusicList.size() > 0 &&
                        mService.getPlayState() <= MusicConstants.MPS_INVALID )
                    mService.play(pos);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDownloadComplete(String url, String filePath, String index) {
        super.onDownloadComplete(url, filePath, index);
        Log.d("jacklam", "video onDownloadComplete url :" + url);
        Log.d("jacklam", "video onDownloadComplete filePath :" + filePath);
        int pos = isMineUrl(url);
        Log.d("jacklam", "video onDownloadComplete isMineUrl:" +pos);
        if(pos >= 0 /*&& data.getId().compareToIgnoreCase(id) == 0*/){
            initSongs(filePath, pos);
            mIndex++;
            if(songs != null && mIndex >= songs.size()){
                mIndex = 0;
            }
            else{
                addDownloadTask(songs.get(mIndex).getUrl(), songs.get(mIndex).getPieceId(), songs.get(mIndex).getId());
//                addDownloadTask(songs.get(mIndex).getUrl(), data.getId(), getAction());

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
        destroyConn();
    }

    private void destroyConn() {
        if(mContext != null && mServiceConnection != null){
            if(mService != null){
                try {
                    mService.exit();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
            mContext.unbindService(mServiceConnection);
//            Intent intent = new Intent(MusicConstants.SERVICE_NAME);
//            intent.setClass(mContext, MediaService.class);
//            mContext.stopService(intent);
        }

    }

    // 不创建view会导致不会有下载回调，配置不要返回坐标信息
    @Override
    public View makeView(Context context, Component item) {

        View view = new View(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(0, 0);
        view.setLayoutParams(lp);

        return view;
    }

    private void initConnection() {
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mService = IMediaService.Stub.asInterface(service);
                if(mService != null) {
                    try {
                        mService.setPlayMode(MusicConstants.MPM_LIST_LOOP_PLAY);
                        mCurMode = mService.getPlayMode();
//                        initSongs(songs);
                        mService.getMusicList(mMusicList);

//                        mCurMusicId = mService.getCurMusicId();
//                        mPlayingSongPosition = MusicUtils.seekPosInListById(mMusicList, mCurMusicId);
//                        initListView();
                        if(mItem != null && mItem.getFile() != null && mItem.getFile().size()> 0){
                            songs = mItem.getFile();
                            addDownloadTask(mItem.getFile().get(mIndex).getUrl(), mItem.getFile().get(mIndex).getPieceId(), mItem.getFile().get(mIndex).getId());
//            addDownloadTask(item.getFile().get(mIndex).getUrl(), data.getId(), getAction());

                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Intent intent = new Intent(MusicConstants.SERVICE_NAME);
        intent.setClass(mContext, MediaService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public boolean isSubscribe(){
        return true;
    }
}
