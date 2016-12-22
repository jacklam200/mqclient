package cn.mqclient.entity;

import java.io.Serializable;

/**
 * Created by LinZaixiong on 2016/11/14.
 */

public class PieceMaterialModel implements Serializable ,IBanner{
    private String programmeId;//节目Id
    private String id;
    private String pieceId;//模块Id
    private String materialId;//素材Id
    private String materialName;//素材名称
    private Integer sort;//顺序
    private Integer day = 0;//天
    private Integer hour = 0;;//时
    private Integer minute = 0;;//分
    private Integer seconds = 0;;//秒
    private Integer adaptationType;//自适应方式
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAdaptationType() {
        return adaptationType;
    }

    public void setAdaptationType(Integer adaptationType) {
        this.adaptationType = adaptationType;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public String getPieceId() {
        return pieceId;
    }

    public void setPieceId(String pieceId) {
        this.pieceId = pieceId;
    }

    public String getProgrammeId() {
        return programmeId;
    }

    public void setProgrammeId(String programmeId) {
        this.programmeId = programmeId;
    }

    public Integer getSeconds() {
        return seconds;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getCover() {
        return url;
    }

    @Override
    public String getCover_url() {
        return url;
    }
}
