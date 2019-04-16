package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.SeckillgoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/SeckillGoods")
@SuppressWarnings("all")
@RestController
public class SeckillgoodsController {
    @Reference
    private SeckillgoodsService seckillgoodsService;
    @RequestMapping("/findOne")
    public SeckillGoods findOne(Long id){
        SeckillGoods seckillGoods=seckillgoodsService.findOne(id);
        return seckillGoods;
    }
    @RequestMapping("/add")
    public Result add(@RequestBody SeckillGoods seckillGoods){
        //设置商家id
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(name);
        try {
            seckillgoodsService.add(seckillGoods);
            return new Result(true,"添加成功");
        }catch (RuntimeException e){
            return new Result(false,e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    @RequestMapping("/update")
    public Result update(@RequestBody SeckillGoods seckillGoods){
        try {
            seckillgoodsService.update(seckillGoods);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }
    @RequestMapping("/delete")
    public Result delete(Long[]ids){
        try {
            seckillgoodsService.dele(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody SeckillGoods seckillGoods) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        seckillGoods.setSellerId(name);
        return seckillgoodsService.search(page,rows,seckillGoods);

    }
    }
