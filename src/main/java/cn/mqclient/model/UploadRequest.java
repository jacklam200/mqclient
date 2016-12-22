package cn.mqclient.model;

import com.alibaba.fastjson.JSON;

import cn.mqclient.Log;

import javax.activation.MimetypesFileTypeMap;

import cn.mqclient.async.tool.Request;
import cn.mqclient.entity.UploadData;
import cn.mqclient.http.upload.FileChunk;
import cn.mqclient.service.UploadService;
import cn.mqclient.utils.Md5;

/**
 * Created by LinZaixiong on 2016/10/5.
 */

public class UploadRequest extends Request<UploadData>{
    private static final String TAG = "UploadRequest";
    private FileChunk fileChunk;
    private UploadService uploadService;

    public UploadRequest(FileChunk chunk, String id){
        fileChunk = chunk;
        uploadService = new UploadService(fileChunk, id);
    }

    @Override
    public UploadData run() {
        String name = fileChunk.getFile().getName();
        Log.d(TAG, "file name:" + name);
        long chunks = fileChunk.getChunkSize();
        Log.d(TAG, "totalChunk:" + chunks);
        String md5 = "";
        try{
            md5 = Md5.getMd5File(fileChunk.getFile());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Log.d(TAG, "md5:" + md5);
        long size = fileChunk.getFileSize();
        Log.d(TAG, "size:" + size);
        String type = new MimetypesFileTypeMap().getContentType(fileChunk.getFile());
        Log.d(TAG, "type:" + type);
        String ret = "";
        try {
            for(int chunk = 0; chunk < chunks; chunk++){
                ret = uploadService.uploadImage(chunk);

                Log.d(TAG, "UploadRequest ret:" + ret);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        UploadData data = null;
        try {
            data = JSON.parseObject(ret, UploadData.class);
        }
        catch (Exception e){
            Log.d("jacklam", "upload return parse failed:" + e.getMessage());
            e.printStackTrace();
        }


        return data;
    }


}
