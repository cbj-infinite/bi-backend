package mq;

import com.qcloud.cos.internal.SdkIOUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.lettuce.core.ScriptOutputType;

import java.util.Scanner;

public class DlxDirectProducer {
    private static final String DLX_EXCHANGE="dlx_exchange";
    private static final String WORK_EXCHANGE="direct2_queue";

    public static void main(String[] args) throws Exception{
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("localhost");
        try(Connection connection=factory.newConnection();
            Channel channel= connection.createChannel();){
            //声明死信交换机
            channel.exchangeDeclare(DLX_EXCHANGE,"direct");
            //声明老板死信队列
            String laoban_queue="laoban_queue";
            channel.queueDeclare(laoban_queue,true,false,false,null);
            channel.queueBind(laoban_queue,DLX_EXCHANGE,"laoban");
            //声明外包死信队列
            String waibao_queue="waibao_queue";
            channel.queueDeclare(waibao_queue,true,false,false,null);
            channel.queueBind(waibao_queue,DLX_EXCHANGE,"waibao");
            //老板队列的监听机制
            DeliverCallback deliverCallback1=(consumerTag,delivery)->{
                String message=new String(delivery.getBody(),"UTF-8");
                System.out.println("laoban 处理了"+message);
            };
            //外包处理机制
            DeliverCallback deliverCallback2=(consumerTag,delivery)->{
              String message=new String(delivery.getBody(),"UTF-8");
                System.out.println("waibai 处理了"+delivery.getEnvelope().getRoutingKey()+"的"+message);
            };
            channel.basicConsume(laoban_queue,false,deliverCallback1,consumerTag->{});
            channel.basicConsume(waibao_queue,false,deliverCallback2,consumerTag->{});
            //输入要进行发送的信息
            Scanner scanner=new Scanner(System.in);
            while(scanner.hasNext()){
                String userInput=scanner.nextLine();
                String[] splits=userInput.split(" ");
                if(splits.length<1){
                    continue;
                }
                String message=splits[0];
                String type=splits[1];
                channel.basicPublish(WORK_EXCHANGE,type,null,message.getBytes("UTF-8"));
                System.out.println("Sent message"+message);

            }


        }

    }
}
