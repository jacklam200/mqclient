package cn.mqclient.repository;

import cn.mqclient.async.tool.Request;
import cn.mqclient.entity.UploadData;
import cn.mqclient.http.upload.FileChunk;
import cn.mqclient.model.UploadRequest;
import cn.mqclient.repository.base.Repository;

/**
 * Created by LinZaixiong on 2016/10/5.
 */

public class FileUploadRepository extends Repository<UploadData>{

    public FileUploadRepository(FileChunk chunk, String id){
        super(new UploadRequest(chunk, id));
    }

    public FileUploadRepository(Request<UploadData> request) {
        super(request);
    }



}
