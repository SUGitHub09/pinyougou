package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import entity.SearchOrderParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/SeckillOrder")
@RestController
@SuppressWarnings("all")
public class SeckillOrderController {
    @Reference
    private SeckillOrderService seckillOrderService;
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody SearchOrderParam searchOrderParam){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return seckillOrderService.search(page,rows,searchOrderParam,name);
    }
    @RequestMapping("/findOne")
    public SeckillOrder findOne(String id1){
        Long id=Long.parseLong(id1);
        SeckillOrder seckillOrder = seckillOrderService.findOne(id);
        //设置查出的id
        seckillOrder.setId1(id1);
        return seckillOrder;
    }
    @RequestMapping("/update")
    public Result update(@RequestBody SeckillOrder seckillOrder){
        seckillOrder.setId(Long.valueOf(seckillOrder.getId1()));
        try {
            seckillOrderService.update(seckillOrder);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[]ids){
        try {
            seckillOrderService.dele(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

}
