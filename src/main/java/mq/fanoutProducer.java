package mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class fanoutProducer {
    private static final String EXCGANGE_NAME="fanout_exchange";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection=factory.newConnection();
            Channel channel=connection.createChannel();){
            //声明交换机
            channel.exchangeDeclare(EXCGANGE_NAME,"fanout");
            //开始输入消息
            Scanner scan=new Scanner(System.in);
            while(scan.hasNext()){
                String message=scan.nextLine();
                channel.basicPublish(EXCGANGE_NAME,"",null,message.getBytes("UTF-8"));
                System.out.println("[x] Sent "+message+"'");
            }

        }
    }
}
