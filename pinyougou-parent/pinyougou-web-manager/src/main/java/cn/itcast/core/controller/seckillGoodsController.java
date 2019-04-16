package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.GoodsService;
import cn.itcast.core.service.seckillGoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.GoodsVo;

@RestController
@RequestMapping("seckillGoods")
public class seckillGoodsController {

    @Reference
    private seckillGoodsService seckillGoodsService;
    /*@RequestMapping("/add")
    public Result add(@RequestBody GoodsVo goodsVo){
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsVo.getGoods().setSellerId(name);
            seckillGoodsService.add(goodsVo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsVo goodsVo){
        try {
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsVo.getGoods().setSellerId(name);
            seckillGoodsService.update(goodsVo);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }*/

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody SeckillGoods seckillGoods){
        return seckillGoodsService.search(page,rows,seckillGoods);
    }


    /*@RequestMapping("findOne")
    public GoodsVo findOne(Long id){
        return seckillGoodsService.findOne(id);
    }*/



    @RequestMapping("/updateStatus")
    private Result updateStatus(Long[] ids,String status){
        try {
            seckillGoodsService.updateStatus(ids,status);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
    /*
    @RequestMapping("/delete")
    private Result delete(Long[] ids){
        try {
            seckillGoodsService.delete(ids);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }*/

}
