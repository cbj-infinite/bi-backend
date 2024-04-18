package mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.lettuce.core.ScriptOutputType;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MultiConsumer {
    private static final String QUEUE_NAME="work_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        for(int i=0;i<2;i++){
            final Channel channel=connection.createChannel();
            channel.queueDeclare(QUEUE_NAME,true,false,false,null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            channel.basicQos(1);
            int finalID=i;
            DeliverCallback deliverCallback=(consumerTag,delivery)->{
                String message=new String(delivery.getBody(),"UTF-8");

                try {
                    //对消息进行处理
                    System.out.println("Receid"+finalID+"消费了"+message);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //处理失败则进行消费失败确认
                    channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
                }finally {
                    System.out.println(" [x] Done");
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
                }
            };
            //开启消费监听
            channel.basicConsume(QUEUE_NAME,false,deliverCallback,consumeTag->{});
        }

    }

}
