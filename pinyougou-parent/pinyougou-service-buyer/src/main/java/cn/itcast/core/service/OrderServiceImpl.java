package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import vo.Cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private PayLogDao payLogDao;
    @Override
    public void add(Order order) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(order.getUserId());
        double total=0;
        List<Long> list=new ArrayList<>();
        for (Cart cart : cartList) {
            long l = idWorker.nextId();
            order.setOrderId(l);
            list.add(l);
            List<OrderItem> orderItemList = cart.getOrderItemList();

            double totalFee=0;
            for (OrderItem orderItem : orderItemList) {
                Long itemId = orderItem.getItemId();
                Item item = itemDao.selectByPrimaryKey(itemId);
                orderItem.setId(idWorker.nextId());
                orderItem.setGoodsId(item.getGoodsId());
                orderItem.setOrderId(l);
                orderItem.setTitle(item.getTitle());
                orderItem.setPrice(item.getPrice());
                orderItem.setTotalFee(item.getPrice().multiply(new BigDecimal(orderItem.getNum())));
                totalFee+=orderItem.getTotalFee().doubleValue();
                orderItem.setPicPath(item.getImage());
                orderItem.setSellerId(item.getSellerId());
                orderItemDao.insertSelective(orderItem);
            }
            order.setPayment(new BigDecimal(totalFee));
            order.setPaymentType("1");
            order.setStatus("1");
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            order.setSourceType("2");
            order.setSellerId(cart.getSellerId());
            orderDao.insertSelective(order);
            total+=totalFee;
        }
        PayLog payLog = new PayLog();
        payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
        payLog.setCreateTime(new Date());
        double total1=total*100;
        payLog.setTotalFee(Long.valueOf((long)total1));
        payLog.setUserId(order.getUserId());
        payLog.setTradeState("0");
        payLog.setOrderList(list.toString().replace("[","").replace("]",""));
        payLog.setPayType("1");
        payLogDao.insertSelective(payLog);
        redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
        //redisTemplate.boundHashOps("CART").delete(order.getUserId());
    }
}
