package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;
import entity.PageResult;
import entity.SearchOrderParam;

public interface OrderService {
    void add(Order order);

    PageResult search(Integer page, Integer rows, SearchOrderParam searchOrderParam, String name);

    void updateStatus(Long[] ids);
}
