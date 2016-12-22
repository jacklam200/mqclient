package cn.mqclient.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Kanwah on 2016/12/1.
 */

public class PlayLog implements Serializable{

    public static class PlayLogItem{
        private  String programmeId;//节目ID    Guid字符串
        private  String terminalGroupItemName;   //屏幕区域a,b,
        private  int  times;//播放次数
        private  long broadcastStartTime;//开始播放时间
        private  long broadcastEndTime;//结束播放时间

        public String getProgrammeId() {
            return programmeId;
        }

        public void setProgrammeId(String programmeId) {
            this.programmeId = programmeId;
        }

        public String getId() {
            return programmeId;
        }

        public void setId(String id) {
            this.programmeId = id;
        }

        public String getTerminalGroupItemName() {
            return terminalGroupItemName;
        }

        public void setTerminalGroupItemName(String terminalGroupItemName) {
            this.terminalGroupItemName = terminalGroupItemName;
        }

        public int getTimes() {
            return times;
        }

        public void setTimes(int times) {
            this.times = times;
        }

        public long getBroadcastStartTime() {
            return broadcastStartTime;
        }

        public void setBroadcastStartTime(long broadcastStartTime) {
            this.broadcastStartTime = broadcastStartTime;
        }

        public long getBroadcastEndTime() {
            return broadcastEndTime;
        }

        public void setBroadcastEndTime(long broadcastEndTime) {
            this.broadcastEndTime = broadcastEndTime;
        }
    }

    private String WARRANTNO;//授权码
    private List<PlayLogItem> list;//播放列表
    private String seNumber;

    public String getWARRANTNO() {
        return WARRANTNO;
    }

    public void setWARRANTNO(String WARRANTNO) {
        this.WARRANTNO = WARRANTNO;
    }

    public List<PlayLogItem> getList() {
        return list;
    }

    public void setList(List<PlayLogItem> list) {
        this.list = list;
    }

    public String getSeNumber() {
        return seNumber;
    }

    public void setSeNumber(String seNumber) {
        this.seNumber = seNumber;
    }
}
