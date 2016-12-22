package cn.mqclient.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LinZaixiong on 2016/12/21.
 */

public class LayerData implements Serializable{
    private String id;
    private List<BTime> broadcastTimes;
    private List<ComponentData> programmes;

    public static class BTime{

        private  Long broadcastStartTime= 0L;//素材可播放开始时间
        private Long broadcastEndTime= 0L;//素材可播放结束时间

        public Long getBroadcastStartTime() {
            return broadcastStartTime;
        }

        public void setBroadcastStartTime(Long broadcastStartTime) {
            this.broadcastStartTime = broadcastStartTime;
        }

        public Long getBroadcastEndTime() {
            return broadcastEndTime;
        }

        public void setBroadcastEndTime(Long broadcastEndTime) {
            this.broadcastEndTime = broadcastEndTime;
        }
    }

    public List<BTime> getBroadcastTimes() {
        return broadcastTimes;
    }

    public void setBroadcastTimes(List<BTime> broadcastTimes) {
        this.broadcastTimes = broadcastTimes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ComponentData> getProgrammes() {
        return programmes;
    }

    public void setProgrammes(List<ComponentData> programmes) {
        this.programmes = programmes;
    }
}
