package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;

import entity.PageResult;
import entity.SearchOrderParam;
import vo.OrderVo;

public interface OrderService {
    void add(Order order);

    PageResult search(Integer page, Integer rows, SearchOrderParam searchOrderParam, String name);

    void updateStatus(Long[] ids);

    PageResult search(Integer page, Integer rows, OrderVo orderVo);


    Order findOne(Long orderId);


    void delete(Long[] ids);


}
