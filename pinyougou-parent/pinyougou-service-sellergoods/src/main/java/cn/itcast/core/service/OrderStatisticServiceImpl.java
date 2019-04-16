package cn.itcast.core.service;

import cn.itcast.common.utils.DateUtils;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import vo.OrderVo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderStatisticServiceImpl implements OrderStatisticService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private GoodsDao goodsDao;

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, String timeStatus, String name) {
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        //必须 不是 未付款  (必须是付过款的)
        criteria.andStatusNotEqualTo("1");
        if (null != name) {
            //必须是 当前登录商户的所有Order
            criteria.andSellerIdEqualTo(name);
        }
         if (timeStatus != null && !"".equals(timeStatus)) {
            //"1"  -- 日订单
            if ("1".equals(timeStatus)) {
                String[] oneDay = DateUtils.getDayStartAndEndTimePointStr(new Date());
                //日期为现在时间到一天前
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    criteria.andCreateTimeBetween(sdf.parse(oneDay[0]), sdf.parse(oneDay[1]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //"2"  -- 周订单
            } else if ("2".equals(timeStatus)) {
                Date[] weekDate = DateUtils.getWeekStartAndEndDate(new Date());
                //日期为现在时间到一周前
                criteria.andCreateTimeBetween(weekDate[0], weekDate[1]);

                //"3"  -- 月订单
            } else if ("3".equals(timeStatus)) {
                Date[] monthDate = DateUtils.getMonthStartAndEndDate(new Date());

                //日期为现在时间到一月前
                criteria.andCreateTimeBetween(monthDate[0], monthDate[1]);
            }
        }
        //创建itemId集合。
        HashSet<Long> goodsIds = new HashSet<>();

        //对order进行条件查询
        List<Order> orderList = orderDao.selectByExample(orderQuery);
        if (null != orderList && orderList.size() > 0) {
            //循环遍历所有订单
            for (Order order : orderList) {
                //通过外键orderId  查询该订单的所有订单项
                OrderItemQuery orderItemQuery = new OrderItemQuery();
                orderItemQuery.createCriteria().andOrderIdEqualTo(order.getOrderId());
                List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
                //获得所有orderId
                if (null != orderItemList && orderItemList.size() > 0) {
                    for (OrderItem OrderItem : orderItemList) {
                        //拿到当前商家的  所有的goodsId(去重的)
                        goodsIds.add(OrderItem.getGoodsId());
                    }
                }
            }
        }
        List<OrderVo> orderVoList = new ArrayList<>();
        //循环遍历所有goodsId
        for (Long goodsId : goodsIds) {
            //通过外键GoodsId  查询该商家的所有订单项
            OrderItemQuery orderItemQuery = new OrderItemQuery();
            orderItemQuery.createCriteria().andGoodsIdEqualTo(goodsId);
            List<OrderItem> orderItemList = orderItemDao.selectByExample(orderItemQuery);
            OrderVo orderVo = new OrderVo();
            int totalNum = 0;
            double allTotalFee = 0;
            for (OrderItem orderItem : orderItemList) {
                //获得数量
                Integer num = orderItem.getNum();
                totalNum +=num;

                //获得一个订单项的金额
                double value = orderItem.getTotalFee().doubleValue();
                allTotalFee+=value;
            }
            orderVo.setNum(totalNum);
            orderVo.setPaymentVolume(new BigDecimal(allTotalFee).setScale(2,BigDecimal.ROUND_HALF_UP));
            Goods goods = goodsDao.selectByPrimaryKey(goodsId);
            orderVo.setGoodsName(goods.getGoodsName());
            orderVoList.add(orderVo);
        }

        if(null != orderVoList && orderVoList.size()>0){
            //调用方法，将orderVoList进行分页
            List<OrderVo> orderVoPages = orderItemVoListToPage(orderVoList,pageNum,pageSize);
            //返回分页结果集
            return new PageResult(Long.parseLong(String.valueOf(orderVoList.size())),orderVoPages);
        }
        return null;
    }
    //进行分页显示
    private List<OrderVo> orderItemVoListToPage(List<OrderVo> orderVoList,Integer pageNum, Integer pageSize){
        List<OrderVo> orderVos = new ArrayList<>();
        int startIndex = (pageNum-1)*pageSize;
        for (int i = 0; i < pageSize; i++) {
            orderVos.add(orderVoList.get(startIndex));
            startIndex++;
            //索引不能超过数组的最大值, 如果相等，则跳出循环。
            if (startIndex == orderVoList.size()){
                break;
            }
        }
        return orderVos;
    }
}
