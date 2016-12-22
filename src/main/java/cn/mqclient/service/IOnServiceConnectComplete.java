package cn.mqclient.service;

import cn.mqclient.service.aidl.IMediaService;
/**
 * Created by LinZaixiong on 2016/10/15.
 */

public interface IOnServiceConnectComplete {
    public void onServiceConnectComplete(IMediaService service);
}
