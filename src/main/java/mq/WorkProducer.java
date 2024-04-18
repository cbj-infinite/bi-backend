package mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

public class WorkProducer {
    private static final String QUEUE_NAME="work_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory =new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        try(Channel channel= connection.createChannel();){
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String message = scanner.nextLine();
                channel.basicPublish("", QUEUE_NAME,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + message + "'");}
        }
    }

}
