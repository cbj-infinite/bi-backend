package com.yupi.springbootinit.bimq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import static com.yupi.springbootinit.bimq.BiConstant.*;

public class BiInitMain {
    public static void main(String[] args) {
        try{
            //创建工厂
            ConnectionFactory factory=new ConnectionFactory();
            //创建连接
            Connection connection=factory.newConnection();
            //创建通道
            Channel channel=connection.createChannel();
            //声明交换机
            channel.exchangeDeclare(BI_EXCHANGE_NAME,"direct");
            //创建队列
            channel.queueDeclare(BI_QUEUE_NAME,true,false,false,null);
            //将队列绑定到指定的交换机
            channel.queueBind(BI_QUEUE_NAME,BI_EXCHANGE_NAME,BI_ROUTING_KEY);



        }catch (Exception e){
            //异常处理
        }
    }
}
