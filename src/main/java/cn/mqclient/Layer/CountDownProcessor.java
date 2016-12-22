package cn.mqclient.Layer;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cn.mqclient.entity.Component;
import cn.mqclient.widget.CustomDigitalClock;

/**
 * Created by LinZaixiong on 2016/9/28.
 */

public class CountDownProcessor extends BaseProcessor<CustomDigitalClock> {
    public final static String TYPE_STR = "countdown";
    public final static int TYPE_INT = 9;

    public CountDownProcessor(Context context, String template) {
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
    public CustomDigitalClock makeView(Context context, Component item) {
        CustomDigitalClock remainTime = new CustomDigitalClock(context);
        remainTime.setEndTime(60 * 1000);
        remainTime.setClockListener(new CustomDigitalClock.ClockListener() { // register the clock's listener

            @Override
            public void timeEnd() {
                // The clock time is ended.
            }

            @Override
            public void remainFiveMinutes() {
                // The clock time is remain five minutes.
            }
        });
        remainTime.start();
        return remainTime;
    }
}
