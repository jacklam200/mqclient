package cn.mqclient.widget;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;

/**
 * 倒计时
 * Created by xuping on 2016/12/7.
 */

public class CountDownTextView extends TextView {

    Calendar mCalendar;
    private final static String m12 = "h:mm aa";//12时间制
    private final static String m24 = "k:mm";//24小时时间制
    private final static String stopTime = "00:00:00:00";
    private FormatChangeObserver mFormatChangeObserver;
    private Runnable mTicker;//线程
    private Handler mHandler;//消息机制
    private long endTime;//结束时间
    public static long distanceTime;//持续时间
    private ClockListener mClockListener;//时钟事件
    private static boolean isFirst;//是否是第一次运行
    private boolean mTickerStopped;//时间结束
    @SuppressWarnings("unused")
    private String mFormat;//字符串格式

    public CountDownTextView(Context context) {
        super(context);//传入上下文对象
        initClock(context);//初始化时钟
    }

    public CountDownTextView(Context context, AttributeSet attrs) {
        super(context, attrs);//属性
        initClock(context);
    }

    private void initClock(Context context) {
        //首先判断对象是否为空。否则实例化
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        mFormatChangeObserver = new FormatChangeObserver();
        getContext().getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, mFormatChangeObserver);
        setFormat();
    }

    /**
     * 将时间显示在这里面
     */
    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;//此时时间没有停止
        super.onAttachedToWindow();
        mHandler = new Handler();//使用异步消息

        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped)
                    return;
                long currentTime = System.currentTimeMillis();
                if (currentTime / 1000 == endTime / 1000 - 5 * 60) {
                    mClockListener.remainFiveMinutes();
                }
                distanceTime = endTime - currentTime;
                distanceTime /= 1000;
                if (distanceTime == 0) {
                    setText(stopTime);
                    onDetachedFromWindow();
                } else if (distanceTime < 0) {
                    setText(stopTime);
                    onDetachedFromWindow();
                } else {
                    setText(dealTime(distanceTime));
                }
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }

    /**
     * 处理详细的时间
     *
     * @param time
     * @return
     */
    public static Spanned dealTime(long time) {
        Spanned str;
        StringBuffer returnString = new StringBuffer();
        long day = time / (24 * 60 * 60);
        long hours = (time % (24 * 60 * 60)) / (60 * 60);
        long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
        long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
        String dayStr = timeStrFormat(String.valueOf(day));
        String hoursStr = timeStrFormat(String.valueOf(hours));
        String minutesStr = timeStrFormat(String.valueOf(minutes));
        String secondStr = timeStrFormat(String.valueOf(second));
        returnString.append(dayStr).append("天").append(hoursStr).append("小时")
                .append(minutesStr).append("分钟").append(secondStr).append("秒");
        str = Html.fromHtml(returnString.toString());
        if (day >= 10) {
            ((Spannable) str).setSpan(new AbsoluteSizeSpan(15), 2, 3,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((Spannable) str).setSpan(new AbsoluteSizeSpan(15), 5, 7,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((Spannable) str).setSpan(new AbsoluteSizeSpan(15), 9, 11,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((Spannable) str).setSpan(new AbsoluteSizeSpan(15), 13, 14,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            ((Spannable) str).setSpan(new AbsoluteSizeSpan(15), 2, 3,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((Spannable) str).setSpan(new AbsoluteSizeSpan(15), 5, 7,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((Spannable) str).setSpan(new AbsoluteSizeSpan(15), 9, 11,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((Spannable) str).setSpan(new AbsoluteSizeSpan(15), 13, 14,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        // return returnString.toString();
        return str;
    }

    //处理时间格式
    private static String timeStrFormat(String timeStr) {
        switch (timeStr.length()) {
            case 1:
                timeStr = "0" + timeStr;
                break;
        }
        return timeStr;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTickerStopped = true;//时间停止
    }

    //设置结束时间
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    //24小时时间处理
    private boolean get24HourMode() {
        return android.text.format.DateFormat.is24HourFormat(getContext());
    }

    //设置格式
    private void setFormat() {
        if (get24HourMode()) {
            mFormat = m24;
        } else {
            mFormat = m12;
        }
    }

    /**
     * ContentObserver——内容观察者，目的是观察(捕捉)特定Uri引起的数据库的变化，继而做一些相应的处理
     * 重写方法
     */
    private class FormatChangeObserver extends ContentObserver {
        public FormatChangeObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            setFormat();
        }
    }

    public void setClockListener(ClockListener clockListener) {
        this.mClockListener = clockListener;
    }

    public interface ClockListener {
        void timeEnd();

        void remainFiveMinutes();
    }
}
