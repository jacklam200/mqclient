package cn.mqclient;

/**
 * Created by KangXinghua on 2016/9/9.
 */
public class Component {
    private String name;//{string}�ؼ�����Ψһ
    private String label;//{{string}�ؼ���ʾ��ǩ
    private String icon;//{{string}�ؼ�ͼ��·��
    private String type;//{{string}�ؼ����ͣ�ֵ��normal�����Բ����������ʾ�����ҿ��Ե�����С��λ�ã���noLayout�����ڲ��������ʾ��ֻ���������ԣ���noResize�����Բ����������ʾ�����ܵ�����С��
    private Integer left;//{{number}X��
    private Integer top;//number}Y��
    private Integer width;//number}��ȣ������Ϊ1080
    private Integer height;//number}�߶ȣ����߶�Ϊ1920
    private String[] file;//{array}��Դ�ļ��б�[filePath, filePath, ....]
    private String content;//{string}�ı����ݣ�����TEXT�ؼ�
    private String color;//{string}������ɫ
    private String fontFamily;//{string}����
    private String background;//{string}������ɫ
    private Integer animation;//{number}�������ͣ�ֵ�����Զ���
    private Integer speed;//{number}�����ٶȣ�ֵ�����Զ���
    private Integer direction;//{number}�˶�����ֵ�����Զ���
    private String src;//{string}��ҳ��ַ
    private Boolean scroll;//{boolean}�Ƿ���Թ���
    private Boolean toolbar;//{boolean}�Ƿ��������
    private Boolean transparent;//{boolean}�Ƿ�͸��
    private String prefix;//{string}ǰ׺
    private String prefixColor;//{string}ǰ׺��ɫ
    private String deadline;//{string}��ֹʱ��

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getLeft() {
        return left;
    }

    public void setLeft(Integer left) {
        this.left = left;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String[] getFile() {
        return file;
    }

    public void setFile(String[] file) {
        this.file = file;
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
}
