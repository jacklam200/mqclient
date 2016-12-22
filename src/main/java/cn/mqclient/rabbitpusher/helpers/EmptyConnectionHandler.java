package cn.mqclient.rabbitpusher.helpers;


import cn.mqclient.rabbitpusher.rabbitmq.ConnectionHandler;

/**
 * Implementation for {@link ConnectionHandler} that does nothing. This is
 * simply a convenience class
 * @author mono
 *
 */
public class EmptyConnectionHandler implements ConnectionHandler {

	@Override
	public void onConnectionLost() {}

}
