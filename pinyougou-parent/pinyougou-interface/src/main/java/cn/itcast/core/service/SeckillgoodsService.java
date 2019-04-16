package cn.itcast.core.service;

import cn.itcast.core.pojo.seckill.SeckillGoods;
import entity.PageResult;

public interface SeckillgoodsService {
    SeckillGoods findOne(Long id );

    void add(SeckillGoods seckillGoods);

    void update(SeckillGoods seckillGoods);

    PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods);

    void dele(Long[] ids);
}
