package com.lily.flashsale.component;

import com.lily.flashsale.db.dao.FlashsaleActivityDao;
import com.lily.flashsale.db.po.FlashsaleActivity;
import com.lily.flashsale.util.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RedisPreheatRunner implements ApplicationRunner {

    @Autowired
    RedisService redisService;

    @Autowired
    FlashsaleActivityDao flashsaleActivityDao;

    /**
     * 将商品库存同步到 redis 中
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<FlashsaleActivity> flashsaleActivities = flashsaleActivityDao.queryFlashsaleActivitysByStatus(1);
        for (FlashsaleActivity flashsaleActivity : flashsaleActivities) {
            redisService.setValue("stock:" + flashsaleActivity.getId(),
                    (long) flashsaleActivity.getAvailableStock());
        }
    }

}
