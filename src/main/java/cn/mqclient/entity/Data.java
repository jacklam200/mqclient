package cn.mqclient.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LinZaixiong on 2016/9/8.
 */
public class Data<T> implements Serializable{

    public static final String DATA = "\"data\":";

    private List<T> data;
    private List<T> programmeItems;

    public List<T> getProgrammeItems() {
        return programmeItems;
    }

    public void setProgrammeItems(List<T> programmeItems) {
        this.programmeItems = programmeItems;
    }

    public List<T> getData() {
        if(data != null)
            return data;
        else{
            return programmeItems;
        }
    }

    public void setData(List<T> data) {
        this.data = data;
        this.programmeItems = data;
    }
}
