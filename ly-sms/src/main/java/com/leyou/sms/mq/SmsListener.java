package com.leyou.sms.mq;

import com.leyou.sms.pojo.SmsProperties;
import com.leyou.sms.utils.SmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: HuYi.Zhang
 * @create: 2018-08-01 10:20
 **/
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties prop;

    private static final Logger logger = LoggerFactory.getLogger(SmsListener.class);

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ly.sms.verify.queue", durable = "true"),
            exchange = @Exchange(value = "ly.sms.exchange",type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = {"sms.verify.code"}))
    public void listenVerifyCode(Map<String,String> msg){
        String phone = msg.get("phone");
        String code = msg.get("code");
        try {
            if (StringUtils.isNotBlank(phone) && StringUtils.isNotBlank(code)) {
                smsUtils.sendSms(phone, code, prop.getSignName(), prop.getVerifyCodeTemplate());
            }
        }catch (Exception e){
            logger.error("短信发送失败，phone:{}, code:{}", phone, code, e);
        }
    }
}
