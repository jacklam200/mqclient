package cn.mqclient;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * Created by KangXinghua on 2016/8/29.
 */
public class Client {

    public final static String SERIAL_NUMBER = "1";//�ͻ���Ψһ��ʶ
    public final static String CLIENT_NAME = "AND_Client_" + SERIAL_NUMBER;
    public final static String SERVER_NAME = "AND_Server_" + SERIAL_NUMBER;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("192.168.0.126");
//        factory.setUsername("android_client");
//        factory.setPassword("its123");

        factory.setHost("119.29.245.204");
        factory.setUsername("android_client");
        factory.setPassword("t7qRj18ht1Cj1yP0");


        Connection connectionClient = factory.newConnection();
        Channel channelClient = connectionClient.createChannel();

        channelClient.queueDeclare(CLIENT_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Connection connectionServer = factory.newConnection();
        final Channel channelServer = connectionServer.createChannel();
        channelServer.queueDeclare(Client.SERVER_NAME, false, false, false, null);

        Consumer consumer = new DefaultConsumer(channelClient) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
                Command command = JSON.parseObject(message, Command.class);
                command.setState(100);
                try {
                    Thread.sleep(2000);
                    message = JSON.toJSONString(command);
                    channelServer.basicPublish("", Client.SERVER_NAME, null, message.getBytes());
                    System.out.println(" [x] Sent '" + message + "'");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        channelClient.basicConsume(CLIENT_NAME, true, consumer);
    }
}
