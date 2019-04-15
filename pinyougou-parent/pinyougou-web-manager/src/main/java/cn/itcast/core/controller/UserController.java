package cn.itcast.core.controller;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.SellerService;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@SuppressWarnings("all")
@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody(required = false)User user){
        return userService.search(page,rows,user);

    }



    @RequestMapping("/findOneForExcel")
    public Result findOneForExcel(Long id){


        try {
            userService.findOneForExcel(id);
            return new Result(true, "用户数据导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "用户数据导出失败");

        }
    }

    @RequestMapping("/userBlock")
    public Result userBlock(Long id){


        try {
            userService.userBlock(id);
            return new Result(true, "用户冻结成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "用户冻结失败");

        }
    }
}
