package cn.itcast.core.controller;

import cn.itcast.core.service.PayService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private PayService payService;
    @RequestMapping("/createNative")
    public Map<String,String> createNative(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return payService.createNative(name);
    }
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no) throws InterruptedException {
        int x=0;
        while (true) {
            Map<String, String> map = payService.queryPayStatus(out_trade_no);
            if ("NOTPAY".equals(map.get("trade_state"))){

                    Thread.sleep(3000);
                x++;
                if (x>100){
                    return new Result(false,"支付超时");
                }
            }else {
                return new Result(true,"付款成功");

            }
        }
    }
}
