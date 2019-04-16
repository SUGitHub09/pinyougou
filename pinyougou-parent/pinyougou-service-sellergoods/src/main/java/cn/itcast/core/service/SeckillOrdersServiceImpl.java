package cn.itcast.core.service;

import cn.itcast.common.utils.DateUtils;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;

import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.pojo.seckill.SeckillOrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import vo.OrderVo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SeckillOrdersServiceImpl implements SeckillOrdersService {

    @Autowired
    private SeckillOrderDao seckillOrderDao;

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private GoodsDao goodsDao;
    @Override
    public PageResult search(Integer page, Integer rows, OrderVo orderVo ){
        Date date = new Date();
        SeckillOrderQuery seckillOrderQuery = new SeckillOrderQuery();

        SeckillOrderQuery.Criteria criteria = seckillOrderQuery.createCriteria();
        if (orderVo != null) {
            //状态
            if (orderVo.getStatus() != null && !orderVo.getStatus().trim().equals("")) {
                criteria.andStatusEqualTo(orderVo.getStatus().trim());
            }
            //订单日期
            if (orderVo.getCreateTime() != null ) {
                criteria.andCreateTimeEqualTo(orderVo.getCreateTime());
            }
            //时间范围
            if ("1".equals(orderVo.getDate())) {
                Date date1 = DateUtils.getTimesmorning(date);
                criteria.andCreateTimeGreaterThanOrEqualTo(date1);

            }
            if ("2".equals(orderVo.getDate())) {
                Date[] weekStartAndEndDate = DateUtils.getWeekStartAndEndDate(date);
                criteria.andCreateTimeBetween(weekStartAndEndDate[0], weekStartAndEndDate[1]);
            }
            if ("3".equals(orderVo.getDate())) {
                Date[] monthStartAndEndDate = DateUtils.getMonthStartAndEndDate(date);
                criteria.andCreateTimeBetween(monthStartAndEndDate[0], monthStartAndEndDate[1]);
            }
            if (orderVo.getDay() != null && !"".equals(orderVo.getDay())) {
                String day = orderVo.getDay()+" 00:00:00";
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date parse = format.parse(day);
                    Date timesmorning = DateUtils.getTimesmorning(parse);
                    Date timesnight = DateUtils.getTimesnight(parse);
                    criteria.andCreateTimeBetween(timesmorning, timesnight);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        PageHelper.startPage(page, rows);
        Page page1 = (Page) seckillOrderDao.selectByExample(seckillOrderQuery);
        List<OrderVo> orderVoList = new ArrayList<>();
        List<SeckillOrder> result = page1.getResult();
        for (SeckillOrder order : result) {
            OrderVo orderVo1=new OrderVo();
            //private String goodsName;
            Long seckillId = order.getSeckillId();
            SeckillGoods seckillGoods = seckillGoodsDao.selectByPrimaryKey(seckillId);
            Long goodsId = seckillGoods.getGoodsId();
            Goods goods = goodsDao.selectByPrimaryKey(goodsId);
            orderVo1.setGoodsName(goods.getGoodsName());


            //private BigDecimal price;
            orderVo1.setPrice(goods.getPrice());
                //private Integer goodsNum;


            orderVo1.setOrderId(order.getId());
            /**
             * 订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
             */
            //private String sourceType;
            //orderVo1.setSourceType(order.getSourceType());
            /**
             * 订单创建时间
             */
            //private Date createTime;
            orderVo1.setCreateTime(order.getCreateTime());
            orderVo1.setPayment(order.getMoney());
            //private String status;
            orderVo1.setStatus(order.getStatus());
            orderVo1.setSourceType("2");
            orderVoList.add(orderVo1);
        }
        return new PageResult(page1.getTotal(),orderVoList);
    }
}
