package cn.mqclient.rabbitpusher.io;

public interface MessageStream {

	public String read() throws Exception;
	public void write(String message) throws Exception;
	public String getChannel();
	public void setChannel(String channel);
	public void setServerChannel(String channel);
	public void connect() throws Exception;
}
