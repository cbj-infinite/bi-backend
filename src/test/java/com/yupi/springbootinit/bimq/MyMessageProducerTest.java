package com.yupi.springbootinit.bimq;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class MyMessageProducerTest {
    @Resource
    MyMessageProducer messageProducer;

    @Test
    void sendMesage() {
        messageProducer.sendMessage("code_exchange","my_routingKey","你好啊");
    }
}