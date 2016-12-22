package cn.mqclient.rabbitpusher.io;

public abstract class AbstractMessageStream implements MessageStream{

	private String channel;
	private String serverChannel;
	
	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setServerChannel(String channel){
		serverChannel = channel;
	}

	public String getServerChannel(){
		return serverChannel;
	}

	public String getChannel() {
		return channel;
	}


}
