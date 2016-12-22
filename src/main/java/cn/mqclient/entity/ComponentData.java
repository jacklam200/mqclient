package cn.mqclient.entity;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by LinZaixiong on 2016/9/9.
 */
public class ComponentData extends Data<Component> implements Serializable {

    @JSONField(name = "terminalGroupItemName")
    private String template;
    private String id;

    private int sort;
    private long timeLenght;


    public String getTemplate() {
        return template;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public long getTimeLenght() {
        return timeLenght;
    }

    public void setTimeLenght(long timeLenght) {
        this.timeLenght = timeLenght;
    }
}
