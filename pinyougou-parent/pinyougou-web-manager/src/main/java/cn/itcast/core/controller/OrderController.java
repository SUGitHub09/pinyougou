package cn.itcast.core.controller;


import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.OrderVo;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody(required = false) OrderVo orderVo){
        return orderService.search(page,rows,orderVo);
    }

    @RequestMapping("/findOne")
    public Order findOne(Long orderId){
        return orderService.findOne(orderId);
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            orderService.delete(ids);
            return new Result(true,"chenggong");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"shibai");
        }
    }

}
