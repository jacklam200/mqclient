package cn.mqclient.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by KangXinghua on 2016/8/26.
 */
public class Command implements Serializable{
    private static final long serialVersionUID = 1234567890L;
    public static final int CMD_RECEIVE = 100;
    public static final int CMD_DONE = 100;
    public static final int CMD_UNDONE = 90;
    public static final int CMD_FAILED = 90;
    private String id;
    private String cmd;
    private Object data;
    private Integer state;
    @JSONField(serialize = false)
    private String content;
    private String reback;

    private String message;
    private java.lang.String serverName;

    public String getReback() {
        return reback;
    }

    public void setReback(String reback) {
        this.reback = reback;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
