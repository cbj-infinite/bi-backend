package mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class fanoutConsumer {
    private static final String EXCHANGE_NAME="fanout_exchange";

    public static void main(String[] args)throws Exception {
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection=factory.newConnection();
            Channel channel1=connection.createChannel();
            Channel channel2=connection.createChannel();){
            //声明交换机
            channel1.exchangeDeclare(EXCHANGE_NAME,"fanout");
            //声明消息队列
            String xiaored_queue="xiaohong_queue";
            String xiaoming_queue="xiaoming_queue";
            channel1.queueDeclare(xiaored_queue,true,false,false,null);
            channel2.queueDeclare(xiaoming_queue,true,false,false,null);
            channel1.queueBind(xiaored_queue,EXCHANGE_NAME,"");
            channel2.queueBind(xiaoming_queue,EXCHANGE_NAME,"");
            //分别接受消息
            DeliverCallback deliverCallback1=(consumerTag,delivery)->{
              String message=new String(delivery.getBody(),"UTF-8");
                System.out.println("xiaohong Received'"+message+"'");
            };
            DeliverCallback deliverCallback2=(consumerTag,delivery)->{
                String message=new String(delivery.getBody(),"UTF-8");
                System.out.println("xiaoming Received"+message+"'");
            };
            channel1.basicConsume(xiaored_queue,true,deliverCallback1,consumerTag->{});
            channel2.basicConsume(xiaoming_queue,true,deliverCallback2,consumerTag->{});

        }

    }
}
