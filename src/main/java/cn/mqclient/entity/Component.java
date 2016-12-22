package cn.mqclient.entity;

import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import cn.mqclient.Layer.BaseProcessor;
import cn.mqclient.utils.LayerCutManager;

/**
 * Created by KangXinghua on 2016/9/9.
 */
public class Component implements IProcessor{
//    private String name; // {string}控件名，唯一
    private String label; // {{string}控件显示标签
    private String icon; // {{string}控件图标路径
    private String type;// {{string}控件类型，值：normal（可以布局面板上显示，并且可以调整大小、位置），noLayout（不在布局面板显示，只能设置属性），noResize（可以布局面板上显示，不能调整大小）
    private int left;// {{number}X轴
    private int top;// number}Y轴
    private int width;// number}宽度，最大宽度为1080
    private int height;// number}高度，最大高度为1920
    private List<PieceMaterialModel> file;// {array}资源文件列表，[filePath, filePath, ....]
    private String id;// {string} id

    private String content;// {string}文本内容，用于TEXT控件
    private String color;// {string}字体颜色
    private String fontFamily;// {string}字体
    private String background;// {string}背景颜色
    private Integer animation;// {number}动画类型，值可以自定义
    private Integer speed;// {number}动画速度，值可以自定义
    private Integer direction;// {number}运动方向，值可以自定义

    private String src;// {string}网页地址
    private Boolean scroll;// {boolean}是否可以滚动
    private Boolean toolbar;// {boolean}是否带工具条

    private Boolean transparent;// {boolean}是否透明
    private String prefix;// {string}前缀
    private String prefixColor;// {string}前缀颜色
    private String deadline;// {string}截止时间
    private String format; // {string} 时间格式
    private int index; // {number}层次
    @JSONField(serialize = false)
    private BaseProcessor processor;
    @JSONField(serialize = false)
    private Long broadcastStartTime = 0L;//素材可播放开始时间
    @JSONField(serialize = false)
    private Long broadcastEndTime = 0L;//素材可播放结束时间

    public static final String TEMPLATE_MODULE = "module";
    @JSONField(name="name")
    private String template; // 刷新的目标模块

    //columns 开始
    private String terminalGroupId;//终端组Id
    private int coordinateByX;//x坐标
    private int coordinateByY;//y坐标
    private int high;//高
    private Integer areaType;//区域类型
   //模板区域名称
    private String terminalGroupItemName;

    //private java.util.Date broadcastStartTime;//素材可播放开始时间
    //private java.util.Date broadcastEndTime;//素材可播放结束时间


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

    private String groupId;// 用于区分模块id，是否跟之前一样

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCurrentLevel(){

        String ret = "module";

        if(!TextUtils.isEmpty(template)){
            String []splite = template.split(":");
        }

        return ret;
    }

    public List<PieceMaterialModel> getFile() {
        return file;
    }

    public void setFile(List<PieceMaterialModel> file) {
        this.file = file;
    }

    public String getTerminalGroupItemName() {
        return terminalGroupItemName;
    }

    public void setTerminalGroupItemName(String terminalGroupItemName) {
        this.terminalGroupItemName = terminalGroupItemName;
    }

    public Integer getAreaType() {
        return areaType;
    }

    public void setAreaType(Integer areaType) {
        this.areaType = areaType;
    }

    public int getCoordinateByX() {
        return coordinateByX;
    }

    public void setCoordinateByX(int coordinateByX) {
        this.coordinateByX = coordinateByX;
    }

    public int getCoordinateByY() {
        return coordinateByY;
    }

    public void setCoordinateByY(int coordinateByY) {
        this.coordinateByY = coordinateByY;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }


    public String getTerminalGroupId() {
        return terminalGroupId;
    }

    public void setTerminalGroupId(String terminalGroupId) {
        this.terminalGroupId = terminalGroupId;
    }


    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }


    public int getIndex(){
        return index;
    }

    public void setIndex(int index){
        this.index = index;
    }
    public String getName() {
        return template;
    }

    public void setName(String name) {
        this.template = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLeft() {
        int l = left;
        if(l == 0){
            l = coordinateByX;
        }
        return l;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        int t = top;
        if(t == 0){
            t = coordinateByY;
        }
        return t;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        int h = height;
        if(h <= 0)
            h = high;
        return h;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public Integer getAnimation() {
        return animation;
    }

    public void setAnimation(Integer animation) {
        this.animation = animation;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Boolean getScroll() {
        return scroll;
    }

    public void setScroll(Boolean scroll) {
        this.scroll = scroll;
    }

    public Boolean getToolbar() {
        return toolbar;
    }

    public void setToolbar(Boolean toolbar) {
        this.toolbar = toolbar;
    }

    public Boolean getTransparent() {
        return transparent;
    }

    public void setTransparent(Boolean transparent) {
        this.transparent = transparent;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefixColor() {
        return prefixColor;
    }

    public void setPrefixColor(String prefixColor) {
        this.prefixColor = prefixColor;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    @Override
    public void setProcessor(BaseProcessor processor) {
        this.processor = processor;
    }

    @Override
    public BaseProcessor getProcessor() {
        return processor;
    }
}
