package com.lily.flashsale;

import com.lily.flashsale.db.dao.FlashsaleActivityDao;
import com.lily.flashsale.db.mappers.FlashsaleActivityMapper;
import com.lily.flashsale.db.po.FlashsaleActivity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
public class DaoTest {
    @Resource
    private FlashsaleActivityMapper flashsaleActivityMapper;
    @Autowired
    private FlashsaleActivityDao flashsaleActivityDao;

    @Test
    void FlashsaleActivityTest() {
        FlashsaleActivity flashsaleActivity = new FlashsaleActivity();
        flashsaleActivity.setName("lily1");
        flashsaleActivity.setCommodityId(99999L);
        flashsaleActivity.setTotalStock(1999999L);
        flashsaleActivity.setFlashsalePrice(new BigDecimal(1));
        flashsaleActivity.setActivityStatus(16);
        flashsaleActivity.setOldPrice(new BigDecimal(99999));
        flashsaleActivity.setAvailableStock(10000);
        flashsaleActivity.setLockStock(0L);
        flashsaleActivityMapper.insert(flashsaleActivity);
        System.out.println("====>>>>" + flashsaleActivityMapper.selectByPrimaryKey(1L));
    }

    @Test
    void setFlashsaleActivityQuery() {
        List<FlashsaleActivity> flashsaleActivitys = flashsaleActivityDao.queryFlashsaleActivitysByStatus(0);
        System.out.println(flashsaleActivitys.size());
        flashsaleActivitys.stream().forEach(flashsaleActivity -> System.out.println(flashsaleActivity.toString()));
    }
}