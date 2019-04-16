package cn.itcast.core.controller;

import cn.itcast.core.service.StatisticsService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.OrderVo;

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
    @RequestMapping("/findSeller")
    public OrderVo findSeller(){
        OrderVo orderVo = new OrderVo();
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        orderVo.setDates(statisticsService.findSDates(name));
        orderVo.setMoneys(statisticsService.findMoneys(name));
        return orderVo;
    }
}
