package com.lily.flashsale.db.dao;

import com.lily.flashsale.db.po.Order;

public interface OrderDao {

    void insertOrder(Order order);

    Order queryOrder(String orderNo);

    void updateOrder(Order order);
}
