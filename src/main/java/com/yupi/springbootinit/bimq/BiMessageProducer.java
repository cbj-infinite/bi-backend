package com.yupi.springbootinit.bimq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yupi.springbootinit.bimq.BiConstant.BI_EXCHANGE_NAME;
import static com.yupi.springbootinit.bimq.BiConstant.BI_ROUTING_KEY;

@Component
public class BiMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void  sendMessage(String message){
        //将消息发送到指定的交换机
        rabbitTemplate.convertAndSend(BI_EXCHANGE_NAME,BI_ROUTING_KEY,message);
    }
}
