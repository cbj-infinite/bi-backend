package com.yupi.springbootinit.bimq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.AMQConnection;

public class MqInitMain {
    public static void main(String[] args) {
        try{
            //创建工厂
            ConnectionFactory factory=new ConnectionFactory();
            Connection connection=factory.newConnection();
            Channel channel=connection.createChannel();
            //声明交换机
            String EXCHANGE_NAME="code_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME,"direct");
            //创建队列
            String queueName="code_queue";
            channel.queueDeclare(queueName,true,false,false,null);
            //将队列绑定到指定的交换机
            channel.queueBind(queueName,EXCHANGE_NAME,"my_routingKey");



        }catch (Exception e){
        }
    }
}
