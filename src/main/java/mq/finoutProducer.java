package mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class finoutProducer {
    private static final String EXCHANGE_NAME="fanout_exchange";

    public static void main(String[] args) throws  Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection=factory.newConnection();
            Channel channel=connection.createChannel()){
            //创建交换机
            channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
            //输入消息进行发送
            Scanner scan=new Scanner(System.in);
            while(scan.hasNext()){
                String message=scan.nextLine();
                channel.basicPublish(EXCHANGE_NAME,"",null,message.getBytes("UTF-8"));
                System.out.println("[x]' sent"+message+"'");
            }
        }
    }
}
