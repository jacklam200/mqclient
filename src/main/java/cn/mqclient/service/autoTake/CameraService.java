package cn.mqclient.service.autoTake;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceView;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.mqclient.Config;
import cn.mqclient.Layer.common.PSubject;
import cn.mqclient.Log;
import cn.mqclient.async.IResponse;
import cn.mqclient.entity.ComponentData;
import cn.mqclient.entity.UploadData;
import cn.mqclient.http.upload.FileChunk;
import cn.mqclient.provider.Layer;
import cn.mqclient.receiver.PlayReceiver;
import cn.mqclient.repository.FileUploadRepository;
import cn.mqclient.repository.base.Repository;
import cn.mqclient.utils.CaptureScreen;

/**
 * Created by Kanwah on 2016/12/6.
 */

public class CameraService extends Service implements Camera.PictureCallback {
    private static final String TAG = CameraService.class.getSimpleName();

    public static void start(Context context, String id) {
        Intent starter = new Intent(context, CameraService.class);
        starter.putExtra("commandId", id);
        context.startService(starter);
    }


    public static void startRecord(Context context, String id) {

        Intent starter = new Intent(context, CameraService.class);
        starter.putExtra("type", "record");
        starter.putExtra("commandId", id);
        context.startService(starter);
    }

    public static void startProgram(Context context, String id,
                                    String json, Layer layer) {

        Intent starter = new Intent(context, CameraService.class);
        starter.putExtra("type", "program");
        starter.putExtra("commandId", id);
        starter.putExtra(PlayReceiver.JSON_EXTRA, json);
        starter.putExtra(PlayReceiver.LAYER_EXTRA, (Parcelable) layer);
        context.startService(starter);
    }



    private Camera mCamera;

    private boolean isRunning; // 是否已在监控拍照

    private String commandId; // 指令ID

    private Repository repo = null;
    private FileChunk chunk;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WakeLockManager.acquire(this);
        Log.d(TAG, "onStartCommand...");
        if(intent != null && intent.getStringExtra("type") != null){
            String type = intent.getStringExtra("type");
            if(!TextUtils.isEmpty(type)){

                if(type.compareToIgnoreCase("record") == 0){
                    Log.d(this.getClass().getName(), "recordScreen");
                    recordScreen(intent);
                }
                else if(type.compareToIgnoreCase("program") == 0){
                    Log.d(this.getClass().getName(), "program");
                    program(intent);
                }
            }
        }
        else{
            Log.d(this.getClass().getName(), "startTakePic");
            startTakePic(intent);
        }

        return START_NOT_STICKY;
    }

    private void program(Intent intent){
        Log.d(this.getClass().getName(), "program method start");
        final  String json = intent.getStringExtra(PlayReceiver.JSON_EXTRA);
        final Layer lay = intent.getParcelableExtra(PlayReceiver.LAYER_EXTRA);
        Log.d(this.getClass().getName(), "program method start lay:" + lay.getName());
        if(!TextUtils.isEmpty(json)){

            List<ComponentData> clist = JSON.parseArray(json, ComponentData.class);
            Log.d(this.getClass().getName(), "program notify");
            PSubject.getInstance().notify(lay,
                    clist, lay.getName());

        }

        Log.d(this.getClass().getName(), "program method end");
    }

    private void recordScreen(Intent intent) {
        final String fullFileName = FilePathUtil.getMonitorPicPath()
                + System.currentTimeMillis() + "record.png";
        File saveFile =  CaptureScreen.captureScreen(this,fullFileName);
        commandId = intent.getStringExtra("commandId");
        if (saveFile != null) {

//            File saveFile = ImageCompressUtil.convertBmpToFile(bmp,
//                    fullFileName);

            if (saveFile != null) {
                // 上传
                //TODO:
                File file = new File(fullFileName);
                if (file.exists()) {
                    chunk = new FileChunk(file);
                    repo = new FileUploadRepository(chunk, commandId);
                    repo.fetch(Config.URL_UPLOAD, new IResponse<UploadData>() {

                        @Override
                        public void onUiResponse(IResponse<UploadData> iResponse, UploadData uploadData, boolean b) {
                            Log.d("jacklam onUiResponse", fullFileName + " upload data : " + b);
                            //deletePic(fullFileName);
                        }

                        @Override
                        public void onPreUiResponse(IResponse<UploadData> iResponse, UploadData uploadData, boolean b) {
                            Log.d("jacklam onPreUiResponse", fullFileName + " upload data : " + b);
                        }

                        @Override
                        public void onPostUiResponse(IResponse<UploadData> iResponse, UploadData uploadData, boolean b) {
                            Log.d("jacklam onPostUiResponse", fullFileName + " upload data : " + b);
                        }
                    });
                } else {
                    Log.d("jacklam", fullFileName + " upload data didn't exist");
                }

            }
        }
    }

    private void startTakePic(Intent intent) {
        if (!isRunning) {
            Log.d(TAG, "Running...");
            commandId = intent.getStringExtra("commandId");
            CameraWindow.show(this);
            SurfaceView preview = CameraWindow.getDummyCameraView();
            if (!TextUtils.isEmpty(commandId) && preview != null) {

                autoTakePic(preview);
            } else {
                stopSelf();
            }
        }
    }

    private void autoTakePic(SurfaceView preview) {
        Log.d(TAG, "autoTakePic...");
        isRunning = true;
        mCamera = getFacingFrontCamera();
        if (mCamera == null) {
            Log.w(TAG, "getFacingFrontCamera return null");
            stopSelf();
            return;
        }
        try {
            mCamera.setPreviewDisplay(preview.getHolder());
            mCamera.startPreview();// 开始预览
            // 防止某些手机拍摄的照片亮度不够
            Thread.sleep(500);
            takePicture();
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
            stopSelf();
        }
    }


    private void takePicture() throws Exception {
        Log.d(TAG, "takePicture...");
        try {
            mCamera.takePicture(null, null, this);
        } catch (Exception e) {
            Log.d(TAG, "takePicture failed!");
            e.printStackTrace();
            throw e;
        }
    }

    private Camera getFacingFrontCamera() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    return Camera.open(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d(TAG, "onPictureTaken...");
        releaseCamera();
        try {
            // 大于500K，压缩预防内存溢出
            BitmapFactory.Options opts = null;
            if (data.length > 500 * 1024) {
                opts = new BitmapFactory.Options();
                opts.inSampleSize = 2;
            }
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                    opts);
            // 旋转270度
            Bitmap newBitmap = ImageCompressUtil.rotateBitmap(bitmap, 90);
            // 保存
            final String fullFileName = FilePathUtil.getMonitorPicPath()
                    + System.currentTimeMillis() + ".jpeg";
            File saveFile = ImageCompressUtil.convertBmpToFile(newBitmap,
                    fullFileName);
            ImageCompressUtil.recyleBitmap(newBitmap);
            if (saveFile != null) {
                // 上传
                //TODO:
                File file = new File(fullFileName);
                if(file.exists()){
                    chunk = new FileChunk(file);
                    repo = new FileUploadRepository(chunk, commandId);
                    repo.fetch(Config.URL_UPLOAD, new IResponse<UploadData>() {

                        @Override
                        public void onUiResponse(IResponse<UploadData> iResponse, UploadData uploadData, boolean b) {
                            Log.d("jacklam onUiResponse", fullFileName + " upload data : " + b);
//                            deletePic(fullFileName);
                        }

                        @Override
                        public void onPreUiResponse(IResponse<UploadData> iResponse, UploadData uploadData, boolean b) {
                            Log.d("jacklam onPreUiResponse", fullFileName + " upload data : " + b);
                        }

                        @Override
                        public void onPostUiResponse(IResponse<UploadData> iResponse, UploadData uploadData, boolean b) {
                            Log.d("jacklam onPostUiResponse", fullFileName + " upload data : " + b);
                        }
                    });
                }
                else{
                    Log.d("jacklam", fullFileName + " upload data didn't exist" );
                }

//                RequestHttp.uploadMonitorPic(callbackHandler, commandId,
//                        saveFile);
            } else {
                // 保存失败，关闭
                stopSelf();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }

        isRunning = false;
    }

    private Handler callbackHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case TaskStatus.LISTENNERTIMEOUT:
                case TaskStatus.ERROR:
                case TaskStatus.FINISHED:
                    // 请求结束，关闭服务
                    stopSelf();
                    break;
            }
        }
    };

    private boolean deletePic(String path){
        boolean isRet = false;

        if(!TextUtils.isEmpty(path)){
            File file = new File(path);
            if(file.exists()){
                file.delete();
            }
        }

        return isRet;
    }

    // 保存照片
    private boolean savePic(byte[] data, File savefile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(savefile);
            fos.write(data);
            fos.flush();
            fos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            Log.d(TAG, "releaseCamera...");
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy...");
        commandId = null;
        isRunning = false;
        CameraWindow.dismiss();
        FilePathUtil.deleteMonitorUploadFiles();
        releaseCamera();
        WakeLockManager.release();
    }
}
