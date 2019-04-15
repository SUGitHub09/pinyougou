package cn.itcast.core.controller;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import cn.itcast.core.service.SeckillOrdersService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.OrderVo;

@RestController
@RequestMapping("seckillOrders")
public class SeckillOrdersController {
    @Reference
    private SeckillOrdersService seckillOrdersService;

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody(required = false) OrderVo orderVo){
        return seckillOrdersService.search(page,rows,orderVo);
    }



}
