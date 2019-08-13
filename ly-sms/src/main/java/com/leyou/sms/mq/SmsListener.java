package com.leyou.sms.mq;

import com.leyou.common.utils.JsonUtils;
import com.leyou.sms.config.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * @author: xiepanpan
 * @Date: 2019/8/12
 * @Description:  短信服务mq消费者
 */
@Component
@Slf4j
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;
    @Autowired
    private SmsProperties smsProperties;

    /**
     *
     * @param msg
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = "sms.verify.code.queue",durable = "true"),
            exchange = @Exchange(name = "leyou.sms.exchange",type = ExchangeTypes.TOPIC),
            key = {"sms.verify.code"}
    ))
    public void listenSendVerificationCode(Map<String,String> msg) {
        if (CollectionUtils.isEmpty(msg)) {
            return;
        }
        String phone = msg.remove("phone");
        //发送验证码
        smsUtils.sendSms(phone,smsProperties.getSignName(),smsProperties.getVertifyCodeTemplate(), JsonUtils.serialize(msg));

        //发送短信日志
        log.info("[短信服务]，发送短信验证码，手机号：{}",phone);
    }


}
