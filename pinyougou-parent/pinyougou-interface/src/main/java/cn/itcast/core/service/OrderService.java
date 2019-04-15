package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.template.TypeTemplate;
import entity.PageResult;
import vo.OrderVo;

import java.util.List;
import java.util.Map;

public interface OrderService {

    PageResult search(Integer page, Integer rows, OrderVo orderVo);


    Order findOne(Long orderId);



    void delete(Long[] ids);



}
