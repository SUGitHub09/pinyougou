package cn.itcast.user.controller;

import cn.itcast.common.utils.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Reference
    private UserService userService;

    @RequestMapping("/sendCode")
    public Result sendCode(String phone) {
        try {
            if (PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                userService.sendCode(phone);

            } else {
                return new Result(false, "手机不合法");
            }
            return new Result(true,"注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }
    @RequestMapping("/add")
    public Result add(String smscode, @RequestBody User user){
        try {
            userService.add(smscode,user);
            return new Result(true,"注册成功");
        }catch (RuntimeException e){
            return new Result(false,e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败");
        }
    }
}
