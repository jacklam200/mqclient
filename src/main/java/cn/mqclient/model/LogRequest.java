package cn.mqclient.model;

import android.app.Activity;

import cn.mqclient.entity.http.BaseEntity;
import cn.mqclient.entity.http.TokenEntity;
import cn.mqclient.model.base.BaseRequest;
import cn.mqclient.model.base.Method;
import cn.mqclient.model.base.RequestListener;
import cn.mqclient.utils.SharePref;
import cn.mqclient.utils.SpConstants;

/**
 * Created by Kanwah on 2016/12/1.
 */

public class LogRequest extends BaseRequest<BaseEntity> {
    private static final String GET_URL = "http://consumer.wissage.com/api/v1/playLog/upload";

    private String json;
    public LogRequest(RequestListener requestListener) {
        super(requestListener);
    }

    @Override
    public String getUrl() {
        String token = SharePref.getInstance().getString(SpConstants.TOKEN, "");
        return GET_URL + "?" + "token=" + token;
    }

    @Override
    public int getMethod() {
        return Method.POST;
    }

    public void setParams(String json){
        this.json = json;
    }

    @Override
    public String getParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("json=");
        sb.append(json);
        return sb.toString();
    }
}
