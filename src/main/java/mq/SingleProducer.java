package mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * @author Shier
 */
public class SingleProducer {
    private final static String SINGLE_QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
       //创建连接工厂
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        try(
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel()) {
            channel.queueDeclare(SINGLE_QUEUE_NAME, false, false, false, null);
            String message = "Helolo world";
            channel.basicPublish("", SINGLE_QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("[x] Sent'" + message + "'");
        }

    }
}