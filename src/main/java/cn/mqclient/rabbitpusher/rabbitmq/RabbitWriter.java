package cn.mqclient.rabbitpusher.rabbitmq;

import cn.mqclient.Log;

import java.util.Collection;
import java.util.HashMap;

import cn.mqclient.rabbitpusher.exceptions.ChannelNotFoundException;
import cn.mqclient.rabbitpusher.exceptions.ChannelRegistrationException;
import cn.mqclient.rabbitpusher.exceptions.RabbitInitializationException;
import cn.mqclient.rabbitpusher.helpers.EmptyConnectionHandler;
import cn.mqclient.rabbitpusher.io.Channel;

/**
 * Created by LinZaixiong on 2016/9/8.
 */
public class RabbitWriter {
    /**
     * Handler class to be notified when connection is lost
     */
    private ConnectionHandler connectionHandler;
    /**
     * a mapping of channel-name => {@link Channel}
     */
    private HashMap<String,Channel> channels;
    /**
     * Handler class to be notified when messages arrive
     */
    private MessageReceiver receiver;
    /**
     * The last channel that was registered, since there is usually only one channel, this is a
     * convenience instead of calling {@link #channels}.get('channel name')
     */
    private Channel latestChannel;

    public RabbitWriter() {
        channels = new HashMap<String, Channel>();
        connectionHandler = new EmptyConnectionHandler();
    }

    public void subscribe(Channel channel) {
        if (channels.containsKey(channel.getName())){
            throw new ChannelRegistrationException(channel.getName());
        }
        else{
            channels.put(channel.getName(), channel);
            this.latestChannel = channel;
        }
    }

    public void read(String channel){
        verifyHandlers();
        if (channels.containsKey(channel)){
            Channel chn = channels.get(channel);
            String message = readFromStream(chn);
            Log.d("RabbitReader", "message:" + message);
            if (message != null){
                receiver.onMessagePreReceived(message, channel);
            }
        }
        else{
            throw new ChannelNotFoundException(channel);
        }
    }

    //---------------------------------------------
    // Helper methods
    //---------------------------------------------

    private void verifyHandlers() {
        if (connectionHandler==null || receiver == null){
            throw new RabbitInitializationException("connectionHandler or receiver is null");
        }
    }

    private String readFromStream(Channel channel){
        try {
            return channel.getStream().read();
        } catch (Exception e) {
            connectionHandler.onConnectionLost();
            return null;
        }
    }

    //--------------------------------------------
    // Getters and Setters
    //--------------------------------------------

    public void setConnectionHandler(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public Collection<Channel> getChannels() {
        return channels.values();
    }

    public Channel getLatestChannel() {
        return latestChannel;
    }

    public void setMessageReceiver(MessageReceiver messageReceiver) {
        this.receiver = messageReceiver;
    }

    public MessageReceiver getMessageReceiver() {
        return receiver;
    }

    public String getLastMessage() {
        return receiver.getLastMessage();
    }
}
