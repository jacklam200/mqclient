package cn.mqclient.entity.http;

import android.content.Context;
import android.text.TextUtils;

import java.io.Serializable;

import cn.mqclient.utils.SharePref;
import cn.mqclient.utils.SpConstants;

/**
 * Created by LinZaixiong on 2016/10/22.
 */

public class MQConfigEntity extends BaseEntity {

    private MQConfig data;

    public MQConfig getData() {
        return data;
    }

    public void setData(MQConfig data) {
        this.data = data;
    }

    public static class MQConfig implements Serializable{
        /**
         * password : t7qRj18ht1Cj1yP0
         * port : 5672
         * host : 119.29.245.204
         * userName : android_client
         */

        private String password;
        private int port;
        private String host;
        private String userName;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void getInfo(){
            port = SharePref.getInstance().getInt(SpConstants.MQ_PORT, 0);
            password = SharePref.getInstance().getString(SpConstants.MQ_PSW, "");
            host = SharePref.getInstance().getString(SpConstants.MQ_HOST, "");
            userName  = SharePref.getInstance().getString(SpConstants.MQ_USERNAME, "");
        }

        public boolean isEmpty(){
            boolean isEmpty = false;

            if(port == 0 || TextUtils.isEmpty(password) || TextUtils.isEmpty(host) ||
                    TextUtils.isEmpty(userName)){

                isEmpty = true;
            }

            return isEmpty;
        }

        public void saveInfo(){

            SharePref.getInstance().setValue(SpConstants.MQ_PSW, password);
            SharePref.getInstance().setValue(SpConstants.MQ_PORT, port);
            SharePref.getInstance().setValue(SpConstants.MQ_HOST, host);
            SharePref.getInstance().setValue(SpConstants.MQ_USERNAME, userName);
        }

        public boolean isSuccess() {
            boolean bRet = false;
            if(port != 0 && !TextUtils.isEmpty(password) &&
                    !TextUtils.isEmpty(host) && !TextUtils.isEmpty(userName)){
                bRet = true;
            }
            return bRet;
        }
    }

}
