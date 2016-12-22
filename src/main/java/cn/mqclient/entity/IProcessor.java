package cn.mqclient.entity;

import java.io.Serializable;

import cn.mqclient.Layer.BaseProcessor;

/**
 * Created by LinZaixiong on 2016/9/9.
 */
public interface IProcessor extends Serializable {
    void setProcessor(BaseProcessor processor);
    BaseProcessor getProcessor();
}
