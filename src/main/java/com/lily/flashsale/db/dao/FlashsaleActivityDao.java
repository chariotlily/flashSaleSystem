package com.lily.flashsale.db.dao;

import com.lily.flashsale.db.po.FlashsaleActivity;

import java.util.List;

public interface FlashsaleActivityDao {

    public List<FlashsaleActivity> queryFlashsaleActivitysByStatus(int activityStatus);

    public void inertFlashsaleActivity(FlashsaleActivity flashsaleActivity);

    public FlashsaleActivity queryFlashsaleActivityById(long activityId);

    public void updateFlashsaleActivity(FlashsaleActivity flashsaleActivity);

    boolean lockStock(Long flashsaleActivityId);

    boolean deductStock(Long flashsaleActivityId);

    void revertStock(Long flashsaleActivityId);
}
