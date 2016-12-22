package cn.mqclient.Layer;

import android.content.Context;
import android.view.View;
import android.widget.AnalogClock;
import android.widget.TextView;

import cn.mqclient.entity.Component;

/**
 * Created by LinZaixiong on 2016/9/28.
 */

public class ClockProcessor extends BaseProcessor<AnalogClock> {
    public final static String TYPE_STR = "clock";
    public final static int TYPE_INT = 8;

    public ClockProcessor(Context context, String template) {
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
    }

    @Override
    public AnalogClock makeView(Context context, Component item) {
        AnalogClock tv = new AnalogClock(context);
        return tv;
    }
}
