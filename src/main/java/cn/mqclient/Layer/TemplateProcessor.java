package cn.mqclient.Layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import cn.mqclient.App;
import cn.mqclient.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.mqclient.Layer.common.InactivityTimer;
import cn.mqclient.Layer.common.PSubject;
import cn.mqclient.R;
import cn.mqclient.adapter.LayerAdapter;
import cn.mqclient.async.tool.AsyncSession;
import cn.mqclient.async.tool.Request;
import cn.mqclient.async.tool.SingleAsyncSession;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.provider.state.OpStateDao;
import cn.mqclient.provider.state.State;
import cn.mqclient.utils.ThreadPool;
import cn.mqclient.widget.LayerListView;

import static android.R.attr.resource;

/**
 * Created by LinZaixiong on 2016/10/17.
 */

public class TemplateProcessor extends BaseProcessor<LayerListView> implements InactivityTimer.OnTimer {
    public final static String TYPE_STR = "MODULE:";
    public final static int TYPE_INT = 10;

    private LayerAdapter mAdapter;
    private LayerListView mq_list;
    private List<Component> mList = new ArrayList<Component>();
    private List<ComponentData> mDataList = new ArrayList<>();
    private int mIndex;


    public TemplateProcessor(Context context, String template) {
        super(context,template);
    }

    @Override
    public int getType() {
        return TYPE_INT;
    }

    @Override
    public void updateView(Context context, Component item) {
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAddView(View parent, View v) {
        super.onAddView(parent, v);

    }

    @Override
    public void onRemoveView(View parent, View v) {
        super.onRemoveView(parent, v);
        exit();


    }

    @Override
    public void notifyDataSetChange(List<ComponentData> list, String template) {
        super.notifyDataSetChange(list, template);

        if(!TextUtils.isEmpty(template)){
            if(template.compareToIgnoreCase(this.template) == 0){
                // 需要清除
                mList.clear();
                mq_list.setBackgroundDrawable(null);
                if(mAdapter != null){
                    mAdapter.notifyDataSetChanged();
                }
                reset();
                mDataList.clear();

                if(list != null && list.size() > 0){
                    mDataList.addAll(list);
                    mList.addAll(mDataList.get(currentIndex()).getProgrammeItems());
                    enter(mDataList.get(currentIndex()).getId(), mDataList.get(currentIndex()).getTemplate());
                }

                if(mAdapter != null){
                    mAdapter.notifyDataSetChanged();
                }
             }
        }
    }

    public void notifyDataSetChange(){
        mList.clear();
        mq_list.setBackgroundDrawable(null);
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }

        mList.addAll(mDataList.get(next()).getProgrammeItems());

        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }
    private void reset(){
        mIndex = 0;
    }

    private int  next(){
        mIndex++;
        if(mIndex >= mDataList.size()){
            mIndex = 0;
        }

        return mIndex;
    }

    private int currentIndex(){
        return mIndex;
    }

    @Override
    public LayerListView makeView(Context context, Component item) {
        mq_list = new LayerListView(context);

        mAdapter = new LayerAdapter(context, mList);
        mq_list.setAdapter(mAdapter);
        return mq_list;
    }

    @Override
    public void setBackGround(String url){
        if(!TextUtils.isEmpty(url)){
            Glide.with(mContext).load("file://" + url).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap data, GlideAnimation anim) {
                    // Post your bytes to a background thread and upload them here.
                    BitmapDrawable drawable = new BitmapDrawable(data);
                    if(drawable != null)
                        mq_list.setBackgroundDrawable(drawable);
                }
            });
        }
        else{
            mq_list.setBackgroundDrawable(null);
        }

    }

    private boolean isExpire(long timeMs){

        boolean isRet = false;

        long curMs = System.currentTimeMillis();

        if(data != null && data.getBroadcastStartTime() != null &&
                data.getBroadcastStartTime() != 0 &&
                data.getBroadcastEndTime() != 0 &&
                curMs >= data.getBroadcastEndTime()
                ){
            isRet = true;
        }

        return isRet;
    }

    private void processExpire(long timeMs){

        if(isExpire(timeMs)){// 过期超时不播放此节目

            InactivityTimer.getInstance().cancel();
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                             @Override
                             public void run() {
                                 reset();
                                 exit();
                                 mDataList.clear();
                                 mList.clear();
                                 mq_list.setBackgroundDrawable(null);
                                 if(mAdapter != null){
                                     mAdapter.notifyDataSetChanged();
                                 }
                             }
                         }
            );
//            ComponentData dataArray = new ComponentData();
//            dataArray.setTemplate(data.getTemplate());
//            dataArray.setProgrammeItems(new ArrayList<Component>());
//            dataArray.setData( new ArrayList<Component>());
            Log.d("jacklam", "clear : "+data.getTemplate());
            if(data != null){

                ThreadPool.getInstance().submit(new Runnable() {
                    @Override
                    public void run() {
                        State state = new State("");
                        state.setId(data.getTemplate());
                        state.setTerminalGroupItemName(data.getTemplate());
                        OpStateDao.getInstance(App.getInstance()).delete(state);

                    }
                });

            }
//
//            PSubject.getInstance().notify(dataArray, dataArray.getProgrammeItems(), data.getTemplate());
        }
    }

    @Override
    public void OnTimer(long timeMs) {
        Log.d("jacklam", "timer:" + this);
        playNextPrograme(timeMs);
        processExpire(timeMs);

    }

    private long totalTime = 0L;

    private void playNextPrograme(long timeMs) {

        if(mDataList != null && mDataList.size() > 0){
            totalTime += 1000;
            if(totalTime >= (mDataList.get(currentIndex()).getTimeLenght() * 1000)){
                totalTime = 0;
                notifyDataSetChange();
                enter(mDataList.get(currentIndex()).getId(), mDataList.get(currentIndex()).getTemplate());
            }
        }

    }

    public boolean isNeedTimer(){
        return true;
    }
//    public void Update(List<Component> list, String template){
//        notifyDataSetChange(list, template);
//    }
}
