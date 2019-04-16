package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import entity.SearchOrderParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.OrderVo;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Reference
    private OrderService orderService;


    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody(required = false) SearchOrderParam searchOrderParam){
        //获取当前登录商家
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //返回分页 结果集
        return orderService.search(page,rows,searchOrderParam,name);
    }
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids){
        try {
            orderService.updateStatus(ids);
            return new Result(true,"订单发货提交成功");
        } catch (RuntimeException e1) {
            return new Result(false,e1.getMessage());
        }catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"订单发货失败，请重新发货");
        }
    }
}
