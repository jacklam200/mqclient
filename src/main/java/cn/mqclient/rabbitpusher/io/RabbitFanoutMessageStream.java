package cn.mqclient.rabbitpusher.io;


import cn.mqclient.Log;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.Channel;

import cn.mqclient.rabbitpusher.exceptions.RabbitInitializationException;

public class RabbitFanoutMessageStream extends AbstractMessageStream{

	private Channel channel;
	private Connection connection;
	private ConnectionFactory factory;
	private QueueingConsumer consumer;
	private boolean connected;
	
	public RabbitFanoutMessageStream(String hostName, String name, String psw){
		factory = new ConnectionFactory();
		factory.setHost(hostName);
		factory.setUsername(name);
		factory.setPassword(psw);
	}

	protected ConnectionFactory getFactory(){
		return factory;
	}

	public void connect() throws Exception{
		if (getChannel() == null){
			throw new RabbitInitializationException("channel cannot be null. Maybe you have not called setChannel(String)");
		}
		connection = factory.newConnection();
		channel = connection.createChannel();
		channel.queueDeclare(getChannel(), false, false, false, null);
//		channel.exchangeDeclare(getChannel(), "fanout");
//		String queue = channel.queueDeclare().getQueue();
//		channel.queueBind(queue, getChannel(), "");
		consumer = new QueueingConsumer(channel);
		channel.basicConsume(getChannel(), true,consumer);


		connected = true;
	}
	
	public String read() throws Exception {
		if (!connected){
			connect();
		}
		try {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			Log.d("jacklam", "message:"+ message);
			return message;
		} catch (Exception e) {
			connected = false;
			throw new Exception("connection lost");
		}
	};
	
	@Override
	public void write(String message) throws Exception {
		throw new IllegalStateException("unsuported method");
	}
}
