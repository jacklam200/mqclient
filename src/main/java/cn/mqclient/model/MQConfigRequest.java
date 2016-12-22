package cn.mqclient.model;

import cn.mqclient.entity.http.MQConfigEntity;
import cn.mqclient.entity.http.TokenEntity;
import cn.mqclient.model.base.BaseRequest;
import cn.mqclient.model.base.Method;
import cn.mqclient.model.base.RequestListener;

/**
 * Created by LinZaixiong on 2016/10/22.
 */

public class MQConfigRequest  extends BaseRequest<MQConfigEntity> {
    private static final String GET_URL = "http://consumer.wissage.com/api/v1/rabbitmq/config";

    private String token;

    public MQConfigRequest(RequestListener callback, String token) {
        super(callback);
        this.token = token;
    }

    @Override
    public String getUrl() {
        return GET_URL;
    }

    @Override
    public int getMethod() {
        return Method.GET;
    }

    @Override
    public String getParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("token=");
        sb.append(token);
        return sb.toString();
    }
}
