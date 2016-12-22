package cn.mqclient.Layer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import cn.mqclient.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import cn.mqclient.FullscreenActivity;
import cn.mqclient.Layer.common.InactivityTimer;
import cn.mqclient.R;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.PieceMaterialModel;

/**
 * Created by LinZaixiong on 2016/9/9.
 */
public class ImageProcessor extends BaseProcessor<FrameLayout> implements InactivityTimer.OnTimer {

    public final static String TYPE_STR = "images";
    public final static int TYPE_INT = 1;
    private final int MAX_TIME = 1000 * 3;
    private ImageView mImageView;
    private long totolMs = 0;
    private int mIndex = 0;
    private  List<PieceMaterialModel> model;

    public ImageProcessor(Context context, String template) {
        super(context, template);
    }

    @Override
    public int getType() {
        return TYPE_INT;
    }

    @Override
    public void execute(Context context, Component item) {
        super.execute(context, item);

        if(item != null && item.getFile() != null && item.getFile().size()> 0){
//            for(int i = 0; i  < item.getFile().size(); i++){
//                FilePath file = new FilePath();
//                file.status = FilePath.STATUS_NOTPLAY;
//                file.url = item.getFile().get(i).getUrl();
//                mFileList.add(file);
//                addDownloadTask(item.getFile().get(i).getUrl(), data.getId(), getAction());
//            }

            FilePath file = new FilePath();
            file.status = FilePath.STATUS_NOTPLAY;
            file.url = item.getFile().get(mIndex).getUrl();
            mFileList.add(file);
            addDownloadTask(item.getFile().get(mIndex).getUrl(), item.getFile().get(mIndex).getPieceId(), item.getFile().get(mIndex).getId());

        }

//        if(context instanceof FullscreenActivity){
//            List<PieceMaterialModel> model = item.getFile();
//            if(item != null && model!= null &&
//                    model.size() > 0 ){
//                ((FullscreenActivity)context).setBackGroud(model.get(0).getUrl(), item.getTerminalGroupItemName());
//            }
//        }
    }

    @Override
    public void onAddView(View parent, View v) {
        super.onAddView(parent, v);



    }

    @Override
    public void onRemoveView(View parent, View v) {
        super.onRemoveView(parent, v);

    }
    @Override
    public FrameLayout makeView(Context context, Component item) {

        FrameLayout view = new FrameLayout(context);

        mImageView = new ImageView(context);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(lp);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        view.addView(mImageView);

        return view;
    }

    @Override
    public void updateView(Context context, Component item) {
        model = item.getFile();
//        if(item != null && model != null &&
//                model.size() > 0 && mView != null){
//            Glide.with(context).load(model.get(mIndex).getUrl()).into(mImageView);
//        }

    }

    @Override
    public void OnTimer(long timeMs) {
        totolMs += timeMs;
        if(model != null){

            if(mIndex < model.size() && model.get(mIndex) != null){
               if(totolMs >= getTimes(model.get(mIndex).getDay(),
                                model.get(mIndex).getHour(),
                                model.get(mIndex).getMinute(),
                                model.get(mIndex).getSeconds())){
                   totolMs = 0;
                   mIndex++;
                   if(mIndex >= model.size()){
                       mIndex = 0;
                   }
                   addDownloadTask(model.get(mIndex).getUrl(), model.get(mIndex).getPieceId(), model.get(mIndex).getId());
               }
            }
        }
    }

    @Override
    protected void onDownloadComplete(String url, String filePath, String id) {
        super.onDownloadComplete(url, filePath, id);
        int pos = isMineUrl(url);
        if(pos == mIndex){
            if(data != null){
                if(pos == 0){
                    Glide.with(mContext).load("file://" + filePath).into(mImageView);
                }
                else{
                    Glide.with(mContext).load("file://" + filePath).into(mImageView);
                }

            }

        }

    }

    @Override
    public boolean isSubscribe(){
        return true;
    }

    @Override
    public boolean isNeedTimer(){
        return true;
    }
}
