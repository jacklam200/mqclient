package cn.mqclient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.mqclient.Log;

import java.io.File;

import cn.mqclient.async.IResponse;
import cn.mqclient.entity.UploadData;
import cn.mqclient.http.upload.FileChunk;
import cn.mqclient.repository.FileUploadRepository;
import cn.mqclient.repository.base.Repository;

/**
 * Created by LinZaixiong on 2016/10/4.
 */

public class AsyncActivity extends BaseActivity implements IResponse<UploadData> {
    private static final String TAG ="AsyncActivity";


    private Repository repo = null;
    private FileChunk chunk;
    
    public static void start(Context context) {
        Intent starter = new Intent(context, AsyncActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        File file = new File("/sdcard/2.bmp");
        chunk = new FileChunk(file);
        repo = new FileUploadRepository(chunk, "id");
        repo.fetch(Config.URL_UPLOAD, this);
    }

    @Override
    public void onUiResponse(IResponse<UploadData> thiz, UploadData paramT, boolean isOnLyUi) {
        Log.d(TAG, "onUiResponse" );
    }

    @Override
    public void onPreUiResponse(IResponse<UploadData> thiz, UploadData paramT, boolean isOnLyUi) {
        Log.d(TAG, "onPreUiResponse" );
    }

    @Override
    public void onPostUiResponse(IResponse<UploadData> thiz, UploadData paramT, boolean isOnLyUi) {
        Log.d(TAG, "onPostUiResponse" );
    }
}
