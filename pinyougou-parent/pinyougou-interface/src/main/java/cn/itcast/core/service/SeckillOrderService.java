package cn.itcast.core.service;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import entity.PageResult;
import entity.SearchOrderParam;

public interface SeckillOrderService {
    SeckillOrder findOne(Long id);

    PageResult search(Integer page, Integer rows, SearchOrderParam searchOrderParam,String name);

    void update(SeckillOrder seckillOrder);

    void dele(Long[]ids);
}
