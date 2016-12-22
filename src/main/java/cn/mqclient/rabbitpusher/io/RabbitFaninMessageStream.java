package cn.mqclient.rabbitpusher.io;

import com.rabbitmq.client.*;
import com.rabbitmq.client.Channel;

/**
 * Created by LinZaixiong on 2016/9/8.
 */
public class RabbitFaninMessageStream extends RabbitFanoutMessageStream {

    private Channel serverChannel;

    public RabbitFaninMessageStream(String hostName, String name, String psw) {
        super(hostName, name, psw);
    }

    public void connect() throws Exception{

        if(getFactory() != null){

            if(getServerChannel() != null){

                Connection connectionServer = getFactory().newConnection();
                serverChannel = connectionServer.createChannel();
                serverChannel.queueDeclare(getServerChannel(), false, false, false, null);
            }
        }

        super.connect();
    }

    @Override
    public void write(String message) throws Exception {

        if(serverChannel != null && getChannel() != null){

            serverChannel.basicPublish("", getChannel(), null, message.getBytes());
        }
    }
}
