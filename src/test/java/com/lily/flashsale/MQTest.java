package com.lily.flashsale;

import com.lily.flashsale.mq.RocketMQService;
import com.lily.flashsale.service.FlashsaleActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class MQTest {

    @Autowired
    RocketMQService rocketMQService;

    @Autowired
    FlashsaleActivityService flashsaleActivityService;

    @Test
    public void sendMQTest() throws Exception {
        rocketMQService.sendMessage("test-lily", "Hello World!" + new Date().toString());
    }


}
