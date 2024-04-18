package mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;

public class DlxDirectConsumer {
    private static final String DLX_EXCHANGE="dlx_exchange";
    private static final String WORK_EXCHANGE="direct2_queue";

    public static void main(String[] args)throws Exception {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection=factory.newConnection();
            Channel channel=connection.createChannel();){
            //声明工作交换机
            channel.exchangeDeclare(WORK_EXCHANGE,"direct");
            HashMap<String ,Object> arg=new HashMap<>();
            //指定消息队列对应的死信交换机
            arg.put("x-dead-letter-exchange",DLX_EXCHANGE);
            //指定消息队列的死信队列
            arg.put("x-dead-letter-routing-key","laoban");
            //声明xiaodog消息队列
            String dog_queue="dog_queue";
            channel.queueDeclare(dog_queue,true,false,false,arg);
            channel.queueBind(dog_queue,WORK_EXCHANGE,"dog");
            HashMap<String ,Object> arg2=new HashMap<>();
            //指定消息队列对应的死信交换机
            arg2.put("x-dead-letter-exchange",DLX_EXCHANGE);
            //指定消息队列的死信队列
            arg2.put("x-dead-letter-routing-key","waibao");
            //声明xiaocat消息队列
            String cat_queue="cat_queue";
            channel.queueDeclare(cat_queue,true,false,false,arg2);
            channel.queueBind(cat_queue,WORK_EXCHANGE,"cat");
            //dog监听处理机制
            DeliverCallback deliverCallback=(comsumerTag,delivery)->{
              String message=new String(delivery.getBody(),"UTF-8");
              //对消息进行拒绝处理
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
                System.out.println("Dog 拒绝了 "+delivery.getEnvelope().getRoutingKey()+"message"+message);
            };
            //cat监听处理机制
            DeliverCallback deliverCallback2=(comsumerTag,delivery)->{
                String message=new String(delivery.getBody(),"UTF-8");
                //对消息进行拒绝处理
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
                System.out.println("Cat 拒绝了 "+delivery.getEnvelope().getRoutingKey()+"message"+message);
            };
            channel.basicConsume(dog_queue,false,deliverCallback,consumerTag->{});
            channel.basicConsume(cat_queue,false,deliverCallback2,consumerTag->{});


        }

    }
}
