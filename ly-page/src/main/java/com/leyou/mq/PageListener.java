package com.leyou.mq;

import com.leyou.service.PageService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @auther ff
 * @create 2018-08-01 14:20
 */
@Component
public class PageListener {

    @Autowired
    private PageService pageService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ly.page.create.queue",durable = "true"),
            exchange = @Exchange(
                    name = "ly.item.exchange",type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = {"item.insert","item.update"}
    ))
    public void listenCreate(Long spuId){
        if (spuId!=null){
            pageService.createHtml(spuId);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ly.page.delete.queue",durable = "true"),
            exchange = @Exchange(
                    name = "ly.item.exchange",type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = "item.delete"
    ))
    public void listenDelete(Long spuId){
        if (spuId!=null){
            pageService.deleteHtml(spuId);
        }
    }
}
