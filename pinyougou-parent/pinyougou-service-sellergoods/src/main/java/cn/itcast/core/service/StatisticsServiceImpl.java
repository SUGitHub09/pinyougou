package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.log.PayLogQuery;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import vo.OrderVo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StatisticsServiceImpl implements StatisticsService{
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private TypeTemplateDao typeTemplateDao;
    @Autowired
    private PayLogDao payLogDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private UserDao userDao;

    @Override
    public List<String> findAll() {
        List<String> list = new ArrayList<>();
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        for (TypeTemplate typeTemplate : typeTemplates) {
            list.add(typeTemplate.getName());
        }
        return list;
    }

    @Override
    public String[] findDates() {
        PayLogQuery payLogQuery = new PayLogQuery();
        payLogQuery.createCriteria().andPayTimeIsNotNull();
        payLogQuery.setOrderByClause("pay_time desc");
        List<PayLog> payLogs = payLogDao.selectByExample(payLogQuery);
        List<String> dates = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd");
        for (PayLog payLog : payLogs) {
            if (!dates.contains(payLog.getPayTime())){
                dates.add(dateFormat.format(payLog.getPayTime()));
            }
            if (dates.size()==7){
                break;
            }
        }
        return dates.toArray(new String[0]);
    }

    @Override
    public List<List<Map>> findMapList() {
        PayLogQuery payLogQuery = new PayLogQuery();
        payLogQuery.createCriteria().andPayTimeIsNotNull();
        payLogQuery.setOrderByClause("pay_time desc");
        List<PayLog> payLogs = payLogDao.selectByExample(payLogQuery);
        List<Date> dates = new ArrayList<>();
        List<List<Map>> result = new ArrayList<>();
        for (PayLog payLog : payLogs) {
            if (!dates.contains(payLog.getPayTime())){
                dates.add(payLog.getPayTime());
            }
            if (dates.size()==7){
                break;
            }
        }
        List<String> all = findAll();

        for (Date date : dates) {
            PayLogQuery payLogQuery1 = new PayLogQuery();
            payLogQuery1.createCriteria().andPayTimeEqualTo(date);
            List<PayLog> payLogs1 = payLogDao.selectByExample(payLogQuery1);

            Map<String,Integer> map = new HashMap<>();
            for (String s : all) {
                map.put(s,0);
            }

            for (PayLog payLog : payLogs1) {
                String orderList = payLog.getOrderList();
                String[] split = orderList.split(",");
                for (String s : split) {
                    OrderItemQuery orderItemQuery = new OrderItemQuery();
                    orderItemQuery.createCriteria().andOrderIdEqualTo(Long.parseLong(s));
                    List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
                    for (OrderItem orderItem : orderItemList) {
                        Integer num = orderItem.getNum();
                        Long goodsId = orderItem.getGoodsId();
                        Goods goods = goodsDao.selectByPrimaryKey(goodsId);
                        Long typeTemplateId = goods.getTypeTemplateId();
                        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(typeTemplateId);
                        //赋值map name value
                        num+=map.get(typeTemplate.getName());
                        map.put(typeTemplate.getName(),num);
                    }
                }
            }
            List<Map> mapList = new ArrayList<>();
            for (Map.Entry<String, Integer> stringIntegerEntry : map.entrySet()) {
                Map<String,Object> objectMap = new HashMap<>();
                objectMap.put("name",stringIntegerEntry.getKey());
                objectMap.put("value",stringIntegerEntry.getValue());
                mapList.add(objectMap);
            }

            result.add(mapList);

        }
        return result;
    }

    /*
                                name:'邮件营销',
                                type:'line',
                                stack: '总量',
                                areaStyle: {},
                                data:[120, 132, 101, 134, 90, 230, 210]
    */

    @Override
    public List<Map> setSeries(OrderVo orderVo) {
        List<Map> maps1 = new ArrayList<>();
        String[] names = orderVo.getNames();
        String[] dates = orderVo.getDates();
        List<List<Map>> mapList = orderVo.getMapList();
        for (String name : names) {
            Map<String,Object> map = new HashMap<>();
            map.put("name",name);
            map.put("type","line");
            map.put("stack","总量");
            map.put("areaStyle",new String[0]);
            Integer[] integers = new Integer[dates.length];
            for (int i = 0;i<dates.length;i++) {
                int num = 0;
                List<Map> maps = mapList.get(i);
                for (Map map1 : maps) {
                    if (map1.get("name").equals(name)){
                        Integer value = (Integer) map1.get("value");
                        num+=value;
                    }
                }
                integers[i] = num;
            }
            map.put("data",integers);
            maps1.add(map);
        }
        return maps1;
    }

    @Override
    public String[] findSDates(String name) {
        List<Order> orders = findOrders(name);
        List<String> dates = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd");
        for (Order order : orders) {
            if (!dates.contains(order.getPaymentTime())){
                dates.add(dateFormat.format(order.getPaymentTime()));
            }
            if (dates.size()==7){
                break;
            }
        }
        return dates.toArray(new String[0]);
    }

    @Override
    public Object[] findMoneys(String name) {
        List<Order> orders = findOrders(name);
        List<Date> dates = new ArrayList<>();
        for (Order order : orders) {
            if (!dates.contains(order.getPaymentTime())){
                dates.add(order.getPaymentTime());
            }
            if (dates.size()==7){
                break;
            }
        }
        List<Double> moneys = new ArrayList<>();
        for (Date date : dates) {
            double money = 0.0;
            for (Order order : orders) {
                if (order.getPaymentTime() == date){
                    money+=order.getPayment().doubleValue();
                }
            }
            moneys.add(money);
        }
        return moneys.toArray();
    }

    @Override
    public List<Object> setNums() {
        List<Object> nums = new ArrayList<>();
        List<Order> orders = orderDao.selectByExample(null);
        List<String> list = new ArrayList<>();
        for (Order order : orders) {
            if (!list.contains(order.getUserId())){
                list.add(order.getUserId());
            }
        }
        List<User> users = userDao.selectByExample(null);
        nums.add(list.size());
        nums.add(users.size()-list.size());
        return nums;
    }

    @Override
    public String[] findUserDate() {
        List<String> dates = new ArrayList<>();
        List<User> users = findUser();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd");
        for (User user : users) {
            String format = dateFormat.format(user.getCreated());
            if (!dates.contains(format)){
                dates.add(format);
            }
            if (dates.size()==7){
                break;
            }
        }
        Collections.reverse(dates);
        return dates.toArray(new String[0]);
    }

    @Override
    public Object[] findUserNums(String[] userDate) {
        List<User> user = findUser();
        List<Long> longs = new ArrayList<>();
        List<Date> dates = new ArrayList<>();
        for (User user1 : user) {
            if (!dates.contains(user1.getCreated())){
                dates.add(user1.getCreated());
            }
            if (dates.size()==7){
                break;
            }
        }
        Collections.reverse(dates);
        for (int i = 0;i<7;i++){
            if (i+1==7){
                List<User> users = userDao.selectByExample(null);
                longs.add((long) users.size());
            }else {
                UserQuery userQuery = new UserQuery();
                userQuery.createCriteria().andCreatedLessThan(dates.get(i+1));
                List<User> users = userDao.selectByExample(userQuery);
                longs.add((long) users.size());
            }
        }

        return longs.toArray();
    }

    @Override
    public String[] findOrderDate() {
        List<String> dates = new ArrayList<>();
        List<Order> orders = findOrder();
        DateFormat dateFormat = new SimpleDateFormat("MM-dd");
        for (Order order : orders) {
            String format = dateFormat.format(order.getCreateTime());
            if (!dates.contains(format)){
                dates.add(format);
            }
            if (dates.size()==7){
                break;
            }
        }
        Collections.reverse(dates);
        return dates.toArray(new String[0]);
    }

    @Override
    public Object[] findOrderNums(String[] orderDate) {
        List<Long> longs = new ArrayList<>();
        List<Date> dates = new ArrayList<>();
        List<Order> orders = findOrder();
        for (Order order : orders) {
            if ((!dates.contains(order.getCreateTime()))){
                dates.add(order.getCreateTime());
            }
            if (dates.size()==7){
                break;
            }
        }
        Collections.reverse(dates);
        for (int i = 0;i<7;i++){
            OrderQuery orderQuery = new OrderQuery();
            orderQuery.createCriteria().andCreateTimeEqualTo(dates.get(i));
            List<Order> orderList = orderDao.selectByExample(orderQuery);
            longs.add((long) orderList.size());
        }
        return longs.toArray();
    }


    public List<Order> findOrders(String name){
        name = "qiandu";
        SellerQuery sellerQuery = new SellerQuery();
        sellerQuery.createCriteria().andSellerIdEqualTo(name);
        List<Seller> sellers = sellerDao.selectByExample(sellerQuery);
        OrderQuery orderQuery = new OrderQuery();
        orderQuery.createCriteria().andSellerIdEqualTo(sellers.get(0).getSellerId());
        orderQuery.setOrderByClause("payment_time desc");
        List<Order> orders = orderDao.selectByExample(orderQuery);
        return orders;
    }
    public List<User> findUser(){
        UserQuery userQuery = new UserQuery();
        userQuery.setOrderByClause("created desc");
        List<User> users = userDao.selectByExample(userQuery);
        return users;
    }
    public List<Order> findOrder(){
        OrderQuery orderQuery = new OrderQuery();
        orderQuery.setOrderByClause("create_time desc");
        List<Order> orders = orderDao.selectByExample(orderQuery);
        return orders;
    }
}
