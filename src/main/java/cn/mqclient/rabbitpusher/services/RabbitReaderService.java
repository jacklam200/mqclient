package cn.mqclient.rabbitpusher.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import cn.mqclient.App;
import cn.mqclient.Log;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.concurrent.TimeoutException;

import cn.mqclient.entity.Command;
import cn.mqclient.entity.Data;
import cn.mqclient.entity.http.MQConfigEntity;
import cn.mqclient.http.download.base.DownloadManager;
import cn.mqclient.rabbitpusher.RabbitPublishService;
import cn.mqclient.rabbitpusher.io.MessageStream;
import cn.mqclient.rabbitpusher.rabbitmq.ConnectionHandler;
import cn.mqclient.rabbitpusher.rabbitmq.RabbitReader;
import cn.mqclient.service.autoInstall.InstallController;
import cn.mqclient.utils.KeepAliveTask;
import cn.mqclient.utils.MonitorScreen;
import cn.mqclient.utils.SharePref;
import cn.mqclient.utils.SpConstants;
import cn.mqclient.utils.ThreadPool;

//import cn.mqclient.rabbitpusher.io.Channel;
//import cn.mqclient.rabbitpusher.io.MessageStream;
//import cn.mqclient.rabbitpusher.io.RabbitFaninMessageStream;
//import cn.mqclient.rabbitpusher.io.RabbitFanoutMessageStream;
//import cn.mqclient.rabbitpusher.rabbitmq.ConnectionHandler;
//import cn.mqclient.rabbitpusher.rabbitmq.MessageReceiver;
//import cn.mqclient.rabbitpusher.rabbitmq.RabbitReader;


public abstract class RabbitReaderService extends IntentService implements ConnectionHandler {
	protected static final String INTENT_CONFIG = "config";

	private MQConfigEntity.MQConfig mConfig;
	private String serverName = "";
	private KeepAliveTask keepAlive = new KeepAliveTask();
	private MonitorScreen monitor = new MonitorScreen();
	private String consumeTag = "";
	protected InstallController mInstaller = new InstallController();

	public static String SERIAL_NUMBER = "1";//客户端唯一标识
	public final static String CLIENT_NAME = "AND_Client_";
	public final static String SERVER_NAME = "AND_Server_";

	Connection connectionClient;
	Channel channelClient;

	public RabbitReaderService() {
		super("rabbit reader");
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		monitor.initMonitor(App.getInstance());
		keepAlive.createKeepAlive(this,this, " 惟石", "惟石信息服务",
				KeepAliveTask.NOTIFICATION_ID_MESSAGE);
		if(mInstaller != null && InstallController.isInstall(this, "cn.kanwah.installservice")){
			mInstaller.bind(this);
		}
	}



	private void closeClient(){

		if(connectionClient != null){
			try {
				if(channelClient != null){

					channelClient.basicCancel(consumeTag);
					channelClient.close();
				}

				connectionClient.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			catch (TimeoutException e) {
				e.printStackTrace();
			}
		}
	}

	public void askForPermission() {
		if(Build.VERSION.SDK_INT >= 23){
			if(!Settings.canDrawOverlays(this)){
				Log.d("jacklam", "can't support overlay");
			}
			else{
				Log.d("jacklam", "support overlay");
			}
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		askForPermission();
		monitor.startMonitor(App.getInstance());
		super.onStartCommand(intent, flags, startId);
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onHandleIntent(Intent intent) {
		Log.d("service", "onHandleIntent");


		//jackalm
		mConfig = (MQConfigEntity.MQConfig) intent.getSerializableExtra(INTENT_CONFIG);
		if(mConfig == null){
			Log.d(this.getClass().getName(), "mConfig null");
			return ;
		}
		//notityQueue();
		receiveMQ();
		install();
		waitQueue();
		onServiceClosing();
	}

	private void receiveMQ(){
		try {

//			closeClient();

			final ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("192.168.0.126");
//        factory.setUsername("android_client");
//        factory.setPassword("its123");

//		factory.setHost("119.29.245.204");
//		factory.setUsername("android_client");
//		factory.setPassword("t7qRj18ht1Cj1yP0");

			factory.setHost(mConfig.getHost());
			factory.setUsername(mConfig.getUserName());
			factory.setPassword(mConfig.getPassword());
			factory.setPort(mConfig.getPort());


			connectionClient = factory.newConnection();

			channelClient = connectionClient.createChannel();
			String client = CLIENT_NAME + SharePref.getInstance().getString(SpConstants.WARRANTNO, "");
			Log.d("jacklam", "client:" + client);
			channelClient.queueDeclare(client, true, false, false, null);
			Log.d("[x] Received ", "[*] Waiting for messages. To exit press CTRL+C");

			Connection connectionServer = factory.newConnection();
			final Channel channelServer = connectionServer.createChannel();
			channelServer.queueDeclare(SERVER_NAME, true, false, false, null);
			channelClient.basicQos(1);
			Consumer consumer = new DefaultConsumer(channelClient) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
						throws IOException {
					consumeTag = consumerTag;

					//接收消息后处理
					String message = new String(body, "UTF-8");
					message = URLDecoder.decode(message, "UTF-8");
					Log.d("[x] Received ", "message:" + message);
					Command command = JSON.parseObject(message, Command.class);

					try {
						Thread.sleep(2000);
						String data = "{" + Data.DATA + JSON.toJSONString(command.getData()) + "}";
						command.setContent(data);
						RabbitReaderService.this.onMessageReceived(command, message);
						//处理完成发送消息回服务端
						serverName = command.getServerName();
						command.setState(Command.CMD_RECEIVE);
						RabbitPublishService service = new RabbitPublishService(serverName);
						service.send(message);

					} catch (Exception e) {
						command.setState(Command.CMD_FAILED);
						command.setMessage(e.getMessage());
						message = JSON.toJSONString(command);
						serverName = command.getServerName();
						RabbitPublishService service = new RabbitPublishService(serverName);
						service.send(message);
						Log.d(this.getClass().getName(), "exception:" + e.getMessage());
						e.printStackTrace();
					}

				}
			};
			channelClient.basicConsume(client, true, consumer);
		}catch (Exception e){
			Log.d(this.getClass().getName(), "exception:" + e.getMessage());
		}

	}

	private void install(){

		if(!InstallController.isInstall(this, "cn.kanwah.installservice")){

			boolean isCopy = InstallController.copyApkFromAssets(this, "installservice.apk",
					Environment.getExternalStorageDirectory().getPath() + "/mqclient/service.apk");
			if(isCopy){

				boolean isInstall = InstallController.installService(this,
						Environment.getExternalStorageDirectory().getPath() + "/mqclient/service.apk");

				if(isInstall){
					mInstaller.bind(this);
				}
			}
		}
	}

	private void waitQueue(){
		try {

			synchronized (this){
				wait();
			}

		}
		catch (Exception e){
			e.printStackTrace();
		}

	}

	private void notityQueue(){
		try {

			synchronized (this){
				this.notifyAll();
			}

		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
//		closeServer();
//		closeClient();
		return super.onUnbind(intent);
	}

	public abstract void onConnectionSuccessful();
	
	/**
	 * Handler executed when the connection is lost
	 */
	public abstract void onConnectionLost();
	/**
	 * Handler that is executed when a message is retrieved from the stream
	 * @param message the message read
	 */
	public abstract void onMessageReceived(Command cmd, String message);
	/**
	 * The name of the channel to connect to
	 * @return a string with the channel name
	 */
	public abstract String getChannelName();

	public abstract String getServerChannelName();
	/**
	 * @return the URL of the RabbitMQ
	 */
	public abstract String getHostName();

	public abstract String getUserName();

	public abstract String getUserPsw();
	/**
	 * while this is true, the intent will attempt to read from the stream
	 * if false, it will quit
	 * @return boolean
	 */
	public abstract boolean running();
	
	public void onServiceClosing(){}

	@Override
	public void onDestroy() {
		closeClient();
		Log.d("service", "onDestroy");
		super.onDestroy();
		if(monitor != null){
			monitor.endMonitor(App.getInstance());
			monitor.destroyMonitor(App.getInstance());
		}
		notityQueue();
		if(keepAlive != null)
			keepAlive.destroyKeepService(this, this, KeepAliveTask.NOTIFICATION_ID_MESSAGE);

		if(mInstaller != null && InstallController.isInstall(this, "cn.kanwah.installservice")){
			mInstaller.unBind(this);
		}
	}
}




