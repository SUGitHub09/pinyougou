package cn.itcast.core.service;

import entity.PageResult;
import vo.OrderVo;

public interface SeckillOrdersService {
    PageResult search(Integer page, Integer rows, OrderVo orderVo);
}
