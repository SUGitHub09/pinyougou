package cn.itcast.core.controller;

import cn.itcast.core.service.OrderStatisticService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.SearchOrderParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orderStatistic")
public class OrderStatisticController {
    @Reference
    private OrderStatisticService orderStatisticService;
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, String timeStatus){
        //获取当前登录商家
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //返回分页 结果集
        return orderStatisticService.search(page,rows,timeStatus,name);
    }
}
