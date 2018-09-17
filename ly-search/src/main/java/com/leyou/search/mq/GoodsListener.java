package com.leyou.search.mq;

import com.leyou.search.service.IndexService;
import com.rabbitmq.http.client.domain.ExchangeType;
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
public class GoodsListener {

    @Autowired
    private IndexService indexService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ly.search.create.queue",durable = "true"),
            exchange = @Exchange(
                    name = "ly.item.exchange",type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = {"item.insert","item.update"}
    ))
    public void listenCreate(Long spuId){
        if (spuId!=null){
            indexService.createIndex(spuId);
        }
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "ly.search.delete.queue",durable = "true"),
            exchange = @Exchange(
                    name = "ly.item.exchange",type = ExchangeTypes.TOPIC,
                    ignoreDeclarationExceptions = "true"),
            key = "item.delete"
    ))
    public void listenDelete(Long spuId){
        if (spuId!=null) {
            indexService.deleteIndex(spuId);
        }
    }
}
