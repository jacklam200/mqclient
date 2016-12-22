package cn.mqclient.service.autoTake;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.mqclient.Log;


/**
 * Created by Kanwah on 2016/12/7.
 */

public class ImageCompressUtil {

    public static Bitmap rotateBitmap(Bitmap origin, int orientationDegree) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree);
        // 围绕原地进行旋转
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }

    public static File convertBmpToFile(Bitmap newBitmap, String fullFileName) {
        File f = new File(fullFileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Log.i("ImageCompressUtil", "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return f;
    }

    public static void recyleBitmap(Bitmap newBitmap) {
        if(newBitmap != null){
            if(!newBitmap.isRecycled())
                newBitmap.recycle();
        }
    }
}
