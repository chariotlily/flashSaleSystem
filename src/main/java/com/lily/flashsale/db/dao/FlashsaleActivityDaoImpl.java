package com.lily.flashsale.db.dao;

import com.lily.flashsale.db.mappers.FlashsaleActivityMapper;
import com.lily.flashsale.db.po.FlashsaleActivity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Repository
public class FlashsaleActivityDaoImpl implements FlashsaleActivityDao {

    @Resource
    private FlashsaleActivityMapper flashsaleActivityMapper;

    @Override
    public List<FlashsaleActivity> queryFlashsaleActivitysByStatus(int activityStatus) {
        return flashsaleActivityMapper.queryFlashsaleActivitysByStatus(activityStatus);
    }

    @Override
    public void inertFlashsaleActivity(FlashsaleActivity flashsaleActivity) {
        flashsaleActivityMapper.insert(flashsaleActivity);
    }

    @Override
    public FlashsaleActivity queryFlashsaleActivityById(long activityId) {
        return flashsaleActivityMapper.selectByPrimaryKey(activityId);
    }

    @Override
    public void updateFlashsaleActivity(FlashsaleActivity flashsaleActivity) {
        flashsaleActivityMapper.updateByPrimaryKey(flashsaleActivity);
    }

    @Override
    public boolean lockStock(Long flashsaleActivityId) {
        int result = flashsaleActivityMapper.lockStock( flashsaleActivityId );
        if (result < 1) {
            log.error("Failed to lock stock");
            return false;
        }
        return true;
    }

    @Override
    public boolean deductStock(Long flashsaleActivityId) {
        int result = flashsaleActivityMapper.deductStock(flashsaleActivityId);
        if (result < 1) {
            log.error("Failed to deduct stock");
            return false;
        }
        return true;
    }

    @Override
    public void revertStock(Long flashsaleActivityId) {
        flashsaleActivityMapper.revertStock(flashsaleActivityId);
    }
}
