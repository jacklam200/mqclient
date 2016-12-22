package cn.mqclient.service.autoTake;

import android.content.Context;
import android.view.SurfaceView;
import android.view.WindowManager;

import cn.mqclient.Log;

/**
 * Created by Kanwah on 2016/12/7.
 */

public class CameraWindow {
    private static final String TAG = CameraWindow.class.getSimpleName();

    private static WindowManager windowManager;

    private static Context applicationContext;

    private static SurfaceView dummyCameraView;

    /**
     * 显示全局窗口
     *
     * @param context
     */
    public static void show(Context context) {
        if (applicationContext == null) {
            applicationContext = context.getApplicationContext();
            windowManager = (WindowManager) applicationContext
                    .getSystemService(Context.WINDOW_SERVICE);
            dummyCameraView = new SurfaceView(applicationContext);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.width = 1;
            params.height = 1;
            params.alpha = 0;
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            // 屏蔽点击事件
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            windowManager.addView(dummyCameraView, params);
            Log.d(TAG, TAG + " showing");
        }
    }

    /**
     * @return 获取窗口视图
     */
    public static SurfaceView getDummyCameraView() {
        return dummyCameraView;
    }

    /**
     * 隐藏窗口
     */
    public static void dismiss() {
        try {
            if (windowManager != null && dummyCameraView != null) {
                windowManager.removeView(dummyCameraView);
                Log.d(TAG, TAG + " dismissed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
