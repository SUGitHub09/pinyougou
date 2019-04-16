package cn.itcast.core.controller;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService sellerService;
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody(required = false)Seller seller){
        return sellerService.search(page,rows,seller);

    }
    @RequestMapping("/findOne")
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("/findOneForExcel")
    public Result findOneForExcel(String id){


        try {
            sellerService.findOneForExcel(id);
            return new Result(true, "商品数据导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "商品数据导出失败");

        }
    }

    @RequestMapping("/findOneForOrder")
    public Result findOneForOrder(String id){


        try {
            sellerService.findOneForOrder(id);
            return new Result(true, "订单数据导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "订单数据导出失败");

        }
    }
}
