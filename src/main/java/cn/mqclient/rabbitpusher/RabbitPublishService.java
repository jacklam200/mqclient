package cn.mqclient.rabbitpusher;

import android.text.TextUtils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import cn.mqclient.entity.http.MQConfigEntity;
import cn.mqclient.utils.ThreadPool;

/**
 * Created by LinZaixiong on 2016/12/21.
 */

public class RabbitPublishService implements Runnable {

    public final static String SERVER_NAME = "AND_Server_Android";
    private String serverName = "";
    private MQConfigEntity.MQConfig config = new MQConfigEntity.MQConfig();
    private String msg;
    public RabbitPublishService(String serverName){
        this.serverName = serverName;
    }

    public void send(String message){
        msg = message;
        ThreadPool.getInstance().submit(this);
    }

    @Override
    public void run() {
        config.getInfo();
        if(config.isEmpty()){
            return;
        }
        final ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(config.getHost());
        factory.setUsername(config.getUserName());
        factory.setPassword(config.getPassword());
        factory.setPort(config.getPort());
        try{
            Connection connectionServer = factory.newConnection();
            final Channel channelServer = connectionServer.createChannel();
            channelServer.queueDeclare(SERVER_NAME, true, false, false, null);
            if(!TextUtils.isEmpty(serverName))
                channelServer.basicPublish("", serverName, null, msg.getBytes());

            if(connectionServer != null) {
                try {

                    if (channelServer != null)
                        channelServer.close();
                    connectionServer.close();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
