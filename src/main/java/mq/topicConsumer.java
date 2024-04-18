package mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class topicConsumer {
    private static final String EXCHANGE_NAME="topic_exchange";

    public static void main(String[] args)throws Exception {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection=factory.newConnection();
            Channel channel1=connection.createChannel();
            Channel channel2=connection.createChannel();){
            //声明交换机
            channel1.exchangeDeclare(EXCHANGE_NAME,"topic");
            //声明消息队列
            String fronted_queue="fronted_queue";
            String backend_queue="backend_queue";
            channel1.queueDeclare(fronted_queue,true,false,false,null);
            channel2.queueDeclare(backend_queue,true,false,false,null);
            channel1.queueBind(fronted_queue,EXCHANGE_NAME,"#.前端.#");
            channel2.queueBind(backend_queue,EXCHANGE_NAME,"#.后端.#");
            //分别接受消息
            DeliverCallback deliverCallback1=(consumerTag,delivery)->{
              String message=new String(delivery.getBody(),"UTF-8");
                System.out.println("前端 Received'"+message+"'");
                channel1.basicAck(delivery.getEnvelope().getDeliveryTag(),false);
            };
            DeliverCallback deliverCallback2=(consumerTag,delivery)->{
                String message=new String(delivery.getBody(),"UTF-8");
                System.out.println("后端 Received"+message+"'");
                channel2.basicNack(delivery.getEnvelope().getDeliveryTag(),false,false);
            };
            channel1.basicConsume(fronted_queue,false,deliverCallback1,consumerTag->{});
            channel2.basicConsume(backend_queue,true,deliverCallback2,consumerTag->{});

        }

    }
}
