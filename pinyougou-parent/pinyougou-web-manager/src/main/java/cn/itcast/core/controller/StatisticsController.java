package cn.itcast.core.controller;

import cn.itcast.core.service.StatisticsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.OrderVo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Reference
    private StatisticsService statisticsService;
    @RequestMapping("/findAll")
    public OrderVo findAll(){
        OrderVo orderVo = new OrderVo();
        orderVo.setNames(statisticsService.findAll().toArray(new String[0]));
        orderVo.setDates(statisticsService.findDates());
        orderVo.setMapList(statisticsService.findMapList());
        orderVo.setSeries(statisticsService.setSeries(orderVo));
        return orderVo;
    }
    @RequestMapping("/userHY")
    public OrderVo userHY(){
        OrderVo orderVo = new OrderVo();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd");
        String[] strings = new String[1];
        strings[0] = dateFormat.format(new Date());
        orderVo.setDates(strings);
        orderVo.setNums(statisticsService.setNums());
        return orderVo;
    }
    @RequestMapping("/findUser")
    public OrderVo findUser(){
        OrderVo orderVo = new OrderVo();
        String[] userDate = statisticsService.findUserDate();
        orderVo.setDates(userDate);
        orderVo.setMoneys(statisticsService.findUserNums(userDate));
        return orderVo;
    }
    @RequestMapping("/findOrder")
    public OrderVo findOrder(){
        OrderVo orderVo = new OrderVo();
        String[] orderDate = statisticsService.findOrderDate();
        orderVo.setDates(orderDate);
        orderVo.setMoneys(statisticsService.findOrderNums(orderDate));
        return orderVo;
    }
}
