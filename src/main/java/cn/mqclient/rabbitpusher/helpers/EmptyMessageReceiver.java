package cn.mqclient.rabbitpusher.helpers;


import cn.mqclient.rabbitpusher.rabbitmq.MessageReceiver;

public class EmptyMessageReceiver extends MessageReceiver {

	@Override
	public void onMessageReceived(String message, String channel) {
	}

}
