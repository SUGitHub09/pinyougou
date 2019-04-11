package cn.itcast.user.service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Service;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.*;
import javax.jws.soap.SOAPBinding;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination destination;
    @Autowired
    private UserDao userDao;
    public void sendCode(String phone){
        String s = RandomStringUtils.randomNumeric(6);
        redisTemplate.boundValueOps(phone).set(s,5, TimeUnit.HOURS);
        jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("PhoneNumbers",phone);
                mapMessage.setString("SignName","control");
                mapMessage.setString("TemplateCode","SMS_162546494");
                mapMessage.setString("TemplateParam","{\"code\":\""+s+"\"}");
                return mapMessage;
            }
        });
    }

    @Override
    public void add(String smscode, User user) {
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if (null!=code){
            if (smscode.equals(code)){
                user.setCreated(new Date());
                user.setUpdated(new Date());
                userDao.insertSelective(user);
            }else {
                throw new RuntimeException("验证码错误");
            }
        }else {
            throw new RuntimeException("验证码失效");
        }
    }
}
