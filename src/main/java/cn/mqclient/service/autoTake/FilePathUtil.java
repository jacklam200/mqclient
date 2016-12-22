package cn.mqclient.service.autoTake;

import android.os.Environment;

import java.io.File;

/**
 * Created by Kanwah on 2016/12/7.
 */

public class FilePathUtil {

    public static String getMonitorPicPath() {
        String path = Environment.getExternalStorageDirectory().getPath()+ "/mqclient/autoTake/";
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        return path;
    }

    public static void deleteMonitorUploadFiles() {

    }
}
