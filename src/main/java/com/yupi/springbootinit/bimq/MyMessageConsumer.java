package com.yupi.springbootinit.bimq;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyMessageConsumer {

   @SneakyThrows
   @RabbitListener(queues={"code_queue"},ackMode ="MANUAL")
    public void reciveMessage(String messag, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag){
        //使用日志记录器打印接受到的消息内容
        log.info("receiveMessage message={}",messag);
        channel.basicAck(deliveryTag,false);

    }

}
