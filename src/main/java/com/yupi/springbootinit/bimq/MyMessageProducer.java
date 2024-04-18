package com.yupi.springbootinit.bimq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MyMessageProducer {
    //使用@Resource 注解对rabbitTemplate进行依赖注入
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void  sendMessage(String exchange,String routingKey,String message){
        //使用rabbitTemplate的converAndSend方法将消息发送到指定的交换机
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
    }
}
