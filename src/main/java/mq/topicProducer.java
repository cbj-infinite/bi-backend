package mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Scanner;

public class topicProducer {
    private static final String EXCGANGE_NAME="topic_exchange";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection=factory.newConnection();
            Channel channel=connection.createChannel();){
            //声明交换机
            channel.exchangeDeclare(EXCGANGE_NAME,"topic");
            //开始输入消息
            Scanner scan=new Scanner(System.in);
            while(scan.hasNext()){
                String userInput=scan.nextLine();
                if(userInput.length()<1){
                    continue;
                }
                String [] strs=userInput.split(" ");
                String message=strs[0];
                String type=strs[1];
                channel.basicPublish(EXCGANGE_NAME,type,null,message.getBytes("UTF-8"));
                System.out.println("[x] Sent '"+message+"type:"+type+"'");
            }

        }
    }
}
