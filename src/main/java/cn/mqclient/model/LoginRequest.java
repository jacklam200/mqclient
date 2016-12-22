package cn.mqclient.model;

import android.app.Activity;
import android.content.Context;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;

import cn.mqclient.App;
import cn.mqclient.entity.Command;
import cn.mqclient.entity.http.BaseEntity;
import cn.mqclient.entity.http.TokenEntity;
import cn.mqclient.http.ProgressDownloader;
import cn.mqclient.model.base.BaseRequest;
import cn.mqclient.model.base.Method;
import cn.mqclient.model.base.RequestListener;
import cn.mqclient.utils.AppUtils;
import cn.mqclient.utils.SharePref;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by LinZaixiong on 2016/10/6.
 */

public class LoginRequest extends BaseRequest<TokenEntity> {
    private static final String GET_URL = "http://consumer.wissage.com/api/v1/token";

    private String userName;
    private String psw;
    private String seNumber = "终端编号";//终端编号
    private String name = "名称";//名称
    private Integer orientation = 1;//屏幕方向1竖屏,2横屏
    private String systemType = "系统类型";//系统类型
    private  String clientVession = "终端版本";//终端版本
    private String diskSpace = "磁盘空间";//磁盘空间
    private String latitude = "维";//维度
    private String longitude = "经";//经度
    private Integer width = 1080;//分辨率宽
    private Integer height = 1920;//分辨率
    private String mac = "mac";//mac地址
    private String ip = "ip";//ip地址
    private String counting = "";

    public LoginRequest(RequestListener callback) {
        super(callback);
    }


    @Override
    public String getUrl() {
        return GET_URL;
    }

    @Override
    public int getMethod() {
        return Method.GET;
    }

    public void setParams(Activity context, String userName, String psw, String name){
        this.userName =  userName;
        this.psw = psw;
        reset();
        this.name += name;

        this.clientVession += AppUtils.getAppVersionName(context);
        this.diskSpace += AppUtils.getAvailableMem();
        this.height += AppUtils.getResolutionHeight(context);
        this.ip += AppUtils.getLocalIpAddress(context);
        this.latitude += SharePref.getInstance().getString("latitude", "-1");
        this.longitude += SharePref.getInstance().getString("longitude", "-1");
        this.mac += AppUtils.getLocalMacAddressFromWifiInfo(context);
        this.orientation += AppUtils.getOrientation(context);
        this.seNumber += AppUtils.getSeNumber(context);
        this.systemType += AppUtils.getSystemVersion();
        this.width += AppUtils.getResolutionWidth(context);
        counting = SharePref.getInstance().getString("counting", "");
    }

    public void reset(){
        seNumber = "";//终端编号
        name = "";//名称
        orientation = 1;//屏幕方向1竖屏,2横屏
        systemType = "";//系统类型
        clientVession = "";//终端版本
        diskSpace = "";//磁盘空间
        latitude = "";//维度
        longitude = "";//经度
        width = 1080;//分辨率宽
        height = 1920;//分辨率
        mac = "";//mac地址
        ip = "";//ip地址
    }
    @Override
    public String getParams() {
        StringBuilder sb = new StringBuilder();
        sb.append("userName=");
        sb.append(userName);
        sb.append("&");
        sb.append("password=");
        sb.append(psw);
        sb.append("&");
        sb.append("seNumber=");
        sb.append(seNumber);
        sb.append("&");
        sb.append("name=");
        sb.append(name);
        sb.append("&");
        sb.append("orientation=");
        sb.append(orientation);
        sb.append("&");
        sb.append("systemType=");
        sb.append(systemType);
        sb.append("&");
        sb.append("clientVession=");
        sb.append(clientVession);
        sb.append("&");
        sb.append("diskSpace=");
        sb.append(diskSpace);
        sb.append("&");
        sb.append("latitude=");
        sb.append(latitude);
        sb.append("&");
        sb.append("longitude=");
        sb.append(longitude);
        sb.append("&");
        sb.append("width=");
        sb.append(width);
        sb.append("&");
        sb.append("height=");
        sb.append(height);
        sb.append("&");
        sb.append("mac=");
        sb.append(mac);
        sb.append("&");
        sb.append("ip=");
        sb.append(ip);
        sb.append("&counting=");
        sb.append(counting);
        String ret = "";
        try {
            ret = sb.toString();//URLEncoder.encode(sb.toString(), "utf-8");
        }
        catch (Exception e){
            ret = sb.toString();
            e.printStackTrace();
        }

        return ret;
    }


}
