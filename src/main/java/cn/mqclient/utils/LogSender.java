package cn.mqclient.utils;

import com.alibaba.fastjson.JSON;

import cn.mqclient.async.IResponse;
import cn.mqclient.async.tool.Request;
import cn.mqclient.async.tool.SingleAsyncSession;
import cn.mqclient.entity.PlayLog;
import cn.mqclient.entity.UploadData;
import cn.mqclient.entity.http.BaseEntity;
import cn.mqclient.model.LogRequest;
import cn.mqclient.model.base.BaseRequest;
import cn.mqclient.model.base.RequestListener;
import okhttp3.Response;

/**
 * Created by Kanwah on 2016/12/1.
 */

public class LogSender extends Request<PlayLog> implements IResponse<PlayLog>, RequestListener {

    private boolean isSend = false;
    private  LogRequest request;
    public void send(){
        if(!isSend){
            if(request != null)
                request.cancel();
            isSend = true;
            SingleAsyncSession.getInstance().submit(this, this);
        }
    }

    @Override
    public PlayLog run() {
        return null;
    }

    @Override
    public void onUiResponse(IResponse<PlayLog> iResponse, PlayLog playLog, boolean b) {
        if(b && playLog != null){
            try {
                request = new LogRequest(this);
                request.setParams(JSON.toJSONString(playLog));
                request.start();
            }
            catch (Exception e){
                e.printStackTrace();
                isSend = false;

            }

        }
        else{
            isSend = false;
        }

    }

    @Override
    public void onPreUiResponse(IResponse<PlayLog> iResponse, PlayLog playLog, boolean b) {

    }

    @Override
    public void onPostUiResponse(IResponse<PlayLog> iResponse, PlayLog playLog, boolean b) {

    }

    @Override
    public void success(BaseRequest baseRequest, Response response, BaseEntity baseEntity) {
        isSend = false;
    }

    @Override
    public void failed(BaseRequest baseRequest, String s) {
        isSend = false;
    }
}
