package cn.mqclient.Layer;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.util.List;

import cn.mqclient.FullscreenActivity;
import cn.mqclient.entity.Component;
import cn.mqclient.entity.PieceMaterialModel;

/**
 * Created by LinZaixiong on 2016/9/28.
 */

public class BackGroundProcessor extends BaseProcessor<View> {
    public final static String TYPE_STR = "background";
    public final static int TYPE_INT = 5;

    public BackGroundProcessor(Context context, String template) {
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
    public void onAddView(View parent, View v) {
        super.onAddView(parent, v);
    }

    @Override
    public void onRemoveView(View parent, View v) {
        super.onRemoveView(parent, v);
        if(data != null){

            ((FullscreenActivity)mContext).setBackGroud(null, data.getTerminalGroupItemName());
        }
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
            file.url = item.getFile().get(0).getUrl();
            mFileList.add(file);
            addDownloadTask(item.getFile().get(0).getUrl(), item.getFile().get(0).getPieceId(), item.getFile().get(0).getId());

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
    protected void onDownloadComplete(String url, String filePath, String id) {
        super.onDownloadComplete(url, filePath, id);
        int pos = isMineUrl(url);
        if(pos == 0 /*&& data.getId().compareToIgnoreCase(id) == 0*/){
            if(data != null){

                ((FullscreenActivity)mContext).setBackGroud(filePath, data.getTerminalGroupItemName());
            }

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

    public boolean isSubscribe(){
        return true;
    }
}
