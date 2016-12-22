package cn.mqclient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by KangXinghua on 2016/8/29.
 */
public class Server {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("192.168.0.126");
//        factory.setUsername("android_client");
//        factory.setPassword("its123");

        factory.setHost("119.29.245.204");
        factory.setUsername("android_client");
        factory.setPassword("t7qRj18ht1Cj1yP0");

        Connection connectionServer = factory.newConnection();
        Channel channelServer = connectionServer.createChannel();

        channelServer.queueDeclare(Client.SERVER_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channelServer) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };

        channelServer.basicConsume(Client.SERVER_NAME, true, consumer);

        Connection connectionClinet = factory.newConnection();
        Channel channelClient = connectionClinet.createChannel();
        channelClient.queueDeclare(Client.CLIENT_NAME, false, false, false, null);

        while (true) {
            Thread.sleep(2000);


            String message = getMessage();
            channelClient.basicPublish("", Client.CLIENT_NAME, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }

    }

    private static String getMessage() {
        Command command = new Command();
        command.setId(UUID.randomUUID().toString());
        command.setCmd("ִ执行命令：" + UUID.randomUUID().toString());
        command.setData(getComponentList());
        command.setState(0);
        return JSON.toJSONString(command);
    }

    private static List<Component> getComponentList() {
        List<Component> componentList = JSON.parseObject("" +
                "[    {        \"name\": \"video\",        \"label\": \"视频\",        \"icon\": \"data/comps/assetsideo.png\",        \"type\": \"normal\",        \"left\": 0,        \"top\": 0,        \"width\": 1440,        \"height\": 256,        \"file\": [            \"http://hc31.aipai.com/user/107/35831107/6571752/card/38774421/card.mp4\"        ],        \"id\": \"a4ef7cda-d269-4b8a-91bc-13e6d06f8ec4\",        \"index\": 1    },    {        \"name\": \"images\",        \"label\": \"图片\",        \"icon\": \"data/comps/assets/images.png\",        \"type\": \"normal\",        \"left\": 0,        \"top\": 256,        \"width\": 1440,        \"height\": 256,        \"file\": [            \"http://img.bss.csdn.net/201610121109357464.jpg\"        ],        \"id\": \"4082eeda-cb99-4e26-960c-969c3956f4a9\",        \"index\": 2    },    {        \"name\": \"text\",        \"label\": \"字幕\",        \"icon\": \"data/comps/assets/text.png\",        \"type\": \"normal\",        \"left\": 0,        \"top\": 512,        \"width\": 1440,        \"height\": 256,        \"content\": \"这是文本内容12312312\",        \"color\": \"#ff6600\",        \"fontFamily\": \"宋体\",        \"background\": \"#000000\",        \"animation\": 0,        \"speed\": 0,        \"direction\": 0,        \"id\": \"682f3df1-987c-4ad7-8eac-85422cf633dc\",        \"index\": 3    },    {        \"name\": \"web\",        \"label\": \"网页\",        \"icon\": \"data/comps/assets/web.png\",        \"type\": \"normal\",        \"left\": 0,        \"top\": 768,        \"width\": 1440,        \"height\": 256,        \"src\": \"http://info.3g.qq.com/\",        \"scroll\": false,        \"toolbar\": false,        \"id\": \"f673c07d-cdc5-499d-99fe-86bf830c7421\",        \"index\": 4    },    {        \"name\": \"background\",        \"label\": \"背景图片\",        \"icon\": \"data/comps/assets/background.png\",        \"type\": \"noLayout\",        \"file\": [            \"http://assets.honglingjin.cn/assets/img/2016/10/9/EDibIu0aNBYitKpVzS4DY6TV.jpg\"        ],        \"id\": \"097d9e1d-3390-4e64-925c-ca4c0a51503a\",        \"index\": 5    },    {        \"name\": \"audio\",        \"label\": \"背景音乐\",        \"icon\": \"data/comps/assets/audio.png\",        \"type\": \"noLayout\",        \"file\": [            \"http://bbmedia.qq.com/media/game/2006/03/0414beydgame.mp3\"        ],        \"id\": \"928eb674-66db-42f0-824e-e7b907c1a0a8\",        \"index\": 6    },    {        \"name\": \"weather\",        \"label\": \"天气\",        \"icon\": \"data/comps/assets/weather.png\",        \"type\": \"noResize\",        \"left\": 0,        \"top\": 1024,        \"width\": 1440,        \"height\": 256,        \"id\": \"8befbcc4-a4a2-4bac-a900-0960d315394e\",        \"index\": 7    },    {        \"name\": \"clock\",        \"label\": \"时钟\",        \"icon\": \"data/comps/assets/clock.png\",        \"type\": \"noResize\",        \"left\": 0,        \"top\": 1280,        \"width\": 1440,        \"height\": 248,        \"color\": \"rgb(153, 204, 0)\",        \"fontFamily\": \"楷体\",        \"background\": \"#000000\",        \"transparent\": false,        \"id\": \"9404072b-a364-4b53-acca-b727082b47c6\",        \"index\": 8    },    {        \"name\": \"countdown\",        \"label\": \"倒计时\",        \"icon\": \"data/comps/assets/countdown.png\",        \"type\": \"noResize\",        \"left\": 0,        \"top\": 1536,        \"width\": 1440,        \"height\": 192,        \"format\": \"DD-HH mm:ss\",        \"color\": \"#ffffff\",        \"fontFamily\": \"宋体\",        \"background\": \"rgb(255, 0, 0)\",        \"prefix\": \"\",        \"prefixColor\": \"#ffffff\",        \"deadline\": \"\",        \"transparent\": false,        \"id\": \"20b75b07-09c1-48d4-a77f-51edeef0ddd3\",        \"index\": 9    }]",
                new TypeReference<List<Component>>() {
        });
        return componentList;
    }
}
