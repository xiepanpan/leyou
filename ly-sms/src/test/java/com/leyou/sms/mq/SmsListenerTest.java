package com.leyou.sms.mq;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SmsListenerTest {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void testSend() {
        Map<String,String> msg = new HashMap();
        msg.put("phone","18554006697");
        msg.put("code","521521");
        amqpTemplate.convertAndSend("leyou.sms.exchange","sms.verify.code",msg);
        System.out.println("已发送消息");
//        Thread.sleep(10000L);
    }

}