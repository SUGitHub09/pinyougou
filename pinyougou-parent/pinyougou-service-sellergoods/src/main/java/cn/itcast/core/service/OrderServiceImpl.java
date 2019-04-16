package cn.itcast.core.service;


import cn.itcast.common.utils.DateUtils;
import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.PageResult;
import entity.SearchOrderParam;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import vo.Cart;
import vo.OrderVo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private GoodsDao goodsDao;
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

    //修改发货状态
    @Override
    public void updateStatus(Long[] ids) {
        if (ids!=null&&ids.length>0){
            //循环修改订单,将未发货改为已发货
            for (Long id : ids) {
                Order order = orderDao.selectByPrimaryKey(id);
                //获取订单状态
                String status = order.getStatus();
                //如果订单是未发货，则修改状态是已发货状态
                if ("2".equals(status)){
                    order.setStatus("3");
                    orderDao.updateByPrimaryKeySelective(order);
                }else {
                    //如果不是，则返回异常信息
                    throw new RuntimeException("必须选择未发货的订单，才能发货");
                }
            }
        }
    }

    //分页+条件查询
    //查询条件为： 订单支付状态  订单日期范围  和订单创建日期
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, SearchOrderParam searchOrderParam,String name) {
        PageHelper.startPage(pageNum,pageSize);
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        if (null!=name){
            //必须是 当前登录商户的所有Order
            criteria.andSellerIdEqualTo(name);
        }
        if (searchOrderParam.getStatus()!=null && !"".equals(searchOrderParam.getStatus())){
            criteria.andStatusEqualTo(searchOrderParam.getStatus());
        }
        if (searchOrderParam.getTimeStatus()!=null && !"".equals(searchOrderParam.getTimeStatus())){

            //"1"  -- 日订单
            if ("1".equals(searchOrderParam.getTimeStatus())){
                String[] oneDay = DateUtils.getDayStartAndEndTimePointStr(new Date());
                //日期为现在时间到一天前
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    criteria.andCreateTimeBetween(sdf.parse(oneDay[0]), sdf.parse(oneDay[1]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //"2"  -- 周订单
            }else if ("2".equals(searchOrderParam.getTimeStatus())){
                Date[] weekDate = DateUtils.getWeekStartAndEndDate(new Date());
                //日期为现在时间到一周前
                criteria.andCreateTimeBetween(weekDate[0], weekDate[1]);

                //"3"  -- 月订单
            }else if ("3".equals(searchOrderParam.getTimeStatus())){
                Date[] monthDate = DateUtils.getMonthStartAndEndDate(new Date());
                //日期为现在时间到一月前
                criteria.andCreateTimeBetween(monthDate[0], monthDate[1]);
            }
        }
        if (searchOrderParam.getCreateTime()!=null){
            criteria.andCreateTimeEqualTo(searchOrderParam.getCreateTime());
        }
        //对order进行条件查询
        Page<Order> page = (Page<Order>) orderDao.selectByExample(orderQuery);
        List<Order> orderList = page.getResult();
        if (null!=orderList&&orderList.size()>0){
            //调用方法，拿到所有OrderVo的集合
            for (Order order : orderList) {
                order.setSourceType("2");
                order.setOrderIdStr(String.valueOf(order.getOrderId()));
                //通过外键orderId  查询该订单的所有订单项
                OrderItemQuery orderItemQuery = new OrderItemQuery();
                orderItemQuery.createCriteria().andOrderIdEqualTo(order.getOrderId());
                List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
                for (OrderItem orderItem : orderItemList) {
                    Long goodsId = orderItem.getGoodsId();
                    Goods goods = goodsDao.selectByPrimaryKey(goodsId);
                    //将商品的title改成商品名称
                    orderItem.setTitle(goods.getGoodsName());
                }
                order.setOrderItemList(orderItemList);
            }
            PageResult pageResult = new PageResult(page.getTotal(), orderList);
            System.out.println(JSON.toJSONString(pageResult));
            return pageResult;
        }
        return null ;
    }
}
