package com.lily.flashsale.mq;

import com.alibaba.fastjson.JSON;
import com.lily.flashsale.db.dao.OrderDao;
import com.lily.flashsale.db.dao.FlashsaleActivityDao;
import com.lily.flashsale.db.po.Order;
import com.lily.flashsale.util.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RocketMQMessageListener(topic = "flashsale_order", consumerGroup = "flashsale_order_group")
public class OrderConsumer implements RocketMQListener<MessageExt> {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    @Autowired
    RedisService redisService;

    @Override
    @Transactional
    public void onMessage (MessageExt messageExt) {
        //1.解析创建订单请求消息
        String message = new String(messageExt.getBody(), StandardCharsets.UTF_8);
        log.info("接收到创建订单请求：" + message);
        Order order = JSON.parseObject(message, Order.class);
        order.setCreateTime(new Date());
        //2.扣减库存
        boolean lockStockResult = flashsaleActivityDao.lockStock(order.getFlashsaleActivityId());
        if (lockStockResult) {
            //订单状态 0:没有可用库存，无效订单 1:已创建等待付款
            order.setOrderStatus(1);
            // 将用户加入到限购用户中
            redisService.addLimitMember(order.getFlashsaleActivityId(), order.getUserId());
        } else {
            order.setOrderStatus(0);
        }
        //3.插入订单
        orderDao.insertOrder(order);
    }
}
