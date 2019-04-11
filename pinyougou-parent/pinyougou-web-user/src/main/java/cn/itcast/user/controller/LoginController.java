package cn.itcast.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/name")
    public Map<String,Object> showName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        HashMap<String, Object> map = new HashMap<>();
        map.put("loginName",name);
        map.put("curTime",new Date());
        return map;
    }
}
