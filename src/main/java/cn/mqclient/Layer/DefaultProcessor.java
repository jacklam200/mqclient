package cn.mqclient.Layer;

import android.app.ActionBar;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import cn.mqclient.entity.Component;

/**
 * Created by LinZaixiong on 2016/9/9.
 */
public class DefaultProcessor extends BaseProcessor {

    public final static String TYPE_STR = "default";
    public final static int TYPE_INT = 0;

    public DefaultProcessor(Context context, String template) {
        super(context, template);
    }

    @Override
    public int getType() {
        return TYPE_INT;
    }

    @Override
    public View makeView(Context context, Component item) {

        View view = new View(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(0, 0);
        view.setLayoutParams(lp);

        return view;
    }

    @Override
    public void updateView(Context context, Component item) {

    }
}
