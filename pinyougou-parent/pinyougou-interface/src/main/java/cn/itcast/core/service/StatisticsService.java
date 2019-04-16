package cn.itcast.core.service;

import vo.OrderVo;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    List<String> findAll();

    String[] findDates();

    List<List<Map>> findMapList();

    List<Map> setSeries(OrderVo orderVo);

    String[] findSDates(String name);

    Object[] findMoneys(String name);

    List<Object> setNums();

    String[] findUserDate();

    Object[] findUserNums(String[] userDate);

    String[] findOrderDate();

    Object[] findOrderNums(String[] orderDate);
}
