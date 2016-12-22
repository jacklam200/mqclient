package cn.mqclient.Layer;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.mqclient.entity.Component;
import cn.mqclient.widget.RollTitlesTextView;

/**
 * Created by LinZaixiong on 2016/9/9.
 */
public class TextProcessor extends BaseProcessor<TextView>{
    public final static String TYPE_STR = "text";
    public final static int TYPE_INT = 2;

    public TextProcessor(Context context, String template) {
        super(context, template);
    }

    @Override
    public int getType() {
        return TYPE_INT;
    }

    @Override
    public TextView makeView(Context context, Component item) {
        TextView tv = new TextView(context);
        tv.setBackgroundColor(0x00ffffff);
        return tv;
    }

    @Override
    public void updateView(Context context, Component item) {

        if(item != null && mView != null){
            item.getAnimation();
//             mView.setAnimation(item.getAnimation());
            (mView).setText(item.getContent());
//            ((TextView)mView).setTextSize(item.get);
        }

    }
}
