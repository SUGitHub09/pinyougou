package cn.itcast.core.service;

import cn.itcast.common.utils.DateUtils;
import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.pojo.seckill.SeckillOrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.SearchOrderParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    @Autowired
    private SeckillOrderDao seckillOrderDao;
    @Override
    public SeckillOrder findOne(Long id) {
        SeckillOrder seckillOrder = seckillOrderDao.selectByPrimaryKey(id);
        System.out.println(seckillOrder);
        return seckillOrder;

    }

    @Override
    public PageResult search(Integer page, Integer rows, SearchOrderParam searchOrderParam, String name) {
        PageHelper.startPage(page,rows);
        SeckillOrderQuery seckillOrderQuery = new SeckillOrderQuery();
        SeckillOrderQuery.Criteria criteria = seckillOrderQuery.createCriteria();
        criteria.andSellerIdEqualTo(name);
        if (null!=searchOrderParam){
            if (null!=searchOrderParam.getTimeStatus()&&!"".equals(searchOrderParam.getTimeStatus())){
                //"1"  -- 日订单
                if ("1".equals(searchOrderParam.getTimeStatus())){
                    String[] oneDay = DateUtils.getDayStartAndEndTimePointStr(new Date());
                    //日期为现在时间到一天前
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        criteria.andCreateTimeBetween(sdf.parse(oneDay[0]), sdf.parse(oneDay[1]));
                    } catch (Exception e) {
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
            if (null!=searchOrderParam.getCreateTime()&&!"".equals(searchOrderParam.getCreateTime())){
                criteria.andCreateTimeEqualTo(searchOrderParam.getCreateTime());
            }
            if (null!=searchOrderParam.getStatus()&&!"".equals(searchOrderParam.getStatus().trim())){
                criteria.andStatusEqualTo(searchOrderParam.getStatus());
            }

        }

        Page<SeckillOrder>page1= (Page<SeckillOrder>) seckillOrderDao.selectByExample(seckillOrderQuery);
        List<SeckillOrder> page1Result = page1.getResult();
        for (SeckillOrder order : page1Result) {
            order.setId1(String.valueOf(order.getId()));
        }

        return new PageResult(page1.getTotal(),page1Result);
    }

    @Override
    public void update(SeckillOrder seckillOrder) {
        seckillOrderDao.updateByPrimaryKeySelective(seckillOrder);
    }

    @Override
    public void dele(Long[]ids) {
        for (Long id : ids) {
            seckillOrderDao.deleteByPrimaryKey(id);
        }

    }
}
