package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Date;
import java.util.List;

@Service
public class SeckillgoodsServiceImpl implements SeckillgoodsService {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Override
    public SeckillGoods findOne(Long id) {
        SeckillGoods seckillGoods = seckillGoodsDao.selectByPrimaryKey(id);

        return seckillGoods;
    }

    @Override
    public void add(SeckillGoods seckillGoods) {
        //获取goodsid
        Long goodsId = seckillGoods.getGoodsId();
        //设置itemid
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        //设置查询条件
        criteria.andGoodsIdEqualTo(goodsId);
        //设置登录商家查询条件
        criteria.andSellerIdEqualTo(seckillGoods.getSellerId());
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        //如果item.getIsDefault()为"1"就对seckillGoods设置值
        //itemID title price
        Integer num=0;
        Item item1=null;
        if (itemList!=null&&itemList.size()>0){
            for (Item item : itemList) {
                if (item.getSellerId().equals(seckillGoods.getSellerId())){
                    if (item.getIsDefault().equals("1")){
                        //itemid
                        item1=item;
                        seckillGoods.setItemId(item.getId());
                        //price
                        seckillGoods.setPrice(item.getPrice());
                        Integer num1 = item.getNum();
                        num+=num1;
                    }
                    Integer num1 = item.getNum();
                    num+=num1;
                }

            }
        }else {
            throw new RuntimeException("该商品不没有库存信息,不能参加秒杀");
        }

        //剩余库存数
        seckillGoods.setStockCount(num);


        //设置商品图片
        seckillGoods.setSmallPic(item1.getImage());
        if (seckillGoods.getTitle()==null){
            //title
            seckillGoods.setTitle(item1.getTitle());
        }

        //设置添加时间
        seckillGoods.setCreateTime(new Date());
         //更改订单状态
        seckillGoods.setStatus("0");
        //保存秒杀商品
        seckillGoodsDao.insertSelective(seckillGoods);

    }

    @Override
    public void update(SeckillGoods seckillGoods) {
            seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);
    }

    @Override
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {
        //分页插件
        PageHelper .startPage(page,rows);
        //从大到小排序
        PageHelper.orderBy("id desc");
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        //设置商家id 条件
        SeckillGoodsQuery.Criteria criteria = seckillGoodsQuery.createCriteria();
        criteria.andSellerIdEqualTo(seckillGoods.getSellerId());
        if (null!=seckillGoods){
            if (null!=seckillGoods.getStatus()&&!"".equals(seckillGoods.getStatus())){
                criteria.andStatusEqualTo(seckillGoods.getStatus());
            }
        }
        Page<SeckillGoods>page1= (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);

        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Override
    public void dele(Long[] ids) {
        for (Long id : ids) {
            seckillGoodsDao.deleteByPrimaryKey(id);
        }
    }
}
