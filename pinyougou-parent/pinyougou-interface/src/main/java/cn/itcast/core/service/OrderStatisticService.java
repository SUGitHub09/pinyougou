package cn.itcast.core.service;

import entity.PageResult;
import entity.SearchOrderParam;

public interface OrderStatisticService {
    PageResult search(Integer page, Integer rows, String searchOrderParam, String name);
}
