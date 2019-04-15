package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import vo.GoodsVo;
import vo.SeckillGoodsVo;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.*;

@Service
public class seckillGoodsServiceImpl implements seckillGoodsService {
    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicPageAndSolrDestination;
    @Autowired
    private Destination queueSolrDeleteDestination;
    /*@Override
    public void add(GoodsVo goodsVo) {
        goodsVo.getGoods().setAuditStatus("0");
        seckillGoodsDao.insertSelective(goodsVo.getGoods());
        goodsVo.getGoodsDesc().setGoodsId(goodsVo.getGoods().getId());
        goodsDescDao.insertSelective(goodsVo.getGoodsDesc());
        if("1".equals(goodsVo.getGoods().getIsEnableSpec())){
            //库存表
            List<Item> itemList = goodsVo.getItemList();
            for (Item item : itemList) {


                //标题  = 商品名称 + " " + 规格1 + " “ + 规格2 .....

                String title = goodsVo.getGoods().getGoodsName();
                //  {"机身内存":"16G","网络":"联通3G"}
                String spec = item.getSpec();
                Map<String,String> specMap = JSON.parseObject(spec, Map.class);

                Set<Map.Entry<String, String>> entries = specMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title += " " + entry.getValue();
                }
                item.setTitle(title);
                //图片 第一
                //[{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]

                List<Map> images = JSON.parseArray(goodsVo.getGoodsDesc().getItemImages(), Map.class);
                if(null != images && images.size() > 0){
                    item.setImage((String)images.get(0).get("url"));
                }
                //第三级商品分类的ID
                item.setCategoryid(goodsVo.getGoods().getCategory3Id());
                //第三级商品分类的名称
                item.setCategory(itemCatDao.selectByPrimaryKey(goodsVo.getGoods().getCategory3Id()).getName());
                //添加时间
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());

                //外键
                item.setGoodsId(goodsVo.getGoods().getId());
                //商家的ID
                item.setSellerId(goodsVo.getGoods().getSellerId());
                //商家的名称
                item.setSeller(sellerDao.selectByPrimaryKey(goodsVo.getGoods().getSellerId()).getNickName());

                //品牌名称
                item.setBrand(brandDao.selectByPrimaryKey(goodsVo.getGoods().getBrandId()).getName());

                //保存库存 表
                itemDao.insertSelective(item);
            }


        }
    }*/

    @Override
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {
        PageHelper.startPage(page, rows);
        SeckillGoodsQuery seckillGoodsQuery = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = seckillGoodsQuery.createCriteria();
        if (seckillGoods != null) {
            if (seckillGoods.getStatus() != null && !"".equals(seckillGoods.getStatus())) {
                criteria.andStatusEqualTo(seckillGoods.getStatus());
            }

        }
        Page<SeckillGoods> page1 = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(seckillGoodsQuery);
        List<SeckillGoodsVo> seckillGoodsVoList = new ArrayList<>();
        List<SeckillGoods> result = page1.getResult();
        for (SeckillGoods goods : result) {
                SeckillGoodsVo seckillGoodsVo = new SeckillGoodsVo();

                seckillGoodsVo.setId(goods.getId());
                seckillGoodsVo.setGoodsName(goodsDao.selectByPrimaryKey(goods.getGoodsId()).getGoodsName());
                seckillGoodsVo.setTitle(goods.getTitle());
                seckillGoodsVo.setPrice(goods.getPrice());
                seckillGoodsVo.setCostPrice(goods.getCostPrice());
                seckillGoodsVo.setStartTime(goods.getStartTime());
                seckillGoodsVo.setEndTime(goods.getEndTime());
                seckillGoodsVo.setSellerId(goods.getSellerId());
                seckillGoodsVo.setStatus(goods.getStatus());
                seckillGoodsVoList.add(seckillGoodsVo);
            }

        return new PageResult(page1.getTotal(),seckillGoodsVoList);
    }

    /*@Override
    public GoodsVo findOne(Long id) {
        GoodsVo vo = new GoodsVo();
        Goods goods = seckillGoodsDao.selectByPrimaryKey(id);
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(itemQuery);
        vo.setGoods(goods);
        vo.setGoodsDesc(goodsDesc);
        vo.setItemList(items);
        return vo;
    }*/

    /*@Override
    public void update(GoodsVo goodsVo) {
        goodsVo.getGoods().setAuditStatus("0");
        seckillGoodsDao.updateByPrimaryKeySelective(goodsVo.getGoods());
        goodsVo.getGoodsDesc().setGoodsId(goodsVo.getGoods().getId());
        goodsDescDao.updateByPrimaryKeySelective(goodsVo.getGoodsDesc());
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(goodsVo.getGoods().getId());
        itemDao.deleteByExample(itemQuery);
        if("1".equals(goodsVo.getGoods().getIsEnableSpec())){
            //库存表
            List<Item> itemList = goodsVo.getItemList();
            for (Item item : itemList) {


                //标题  = 商品名称 + " " + 规格1 + " “ + 规格2 .....

                String title = goodsVo.getGoods().getGoodsName();
                //  {"机身内存":"16G","网络":"联通3G"}
                String spec = item.getSpec();
                Map<String,String> specMap = JSON.parseObject(spec, Map.class);

                Set<Map.Entry<String, String>> entries = specMap.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title += " " + entry.getValue();
                }
                item.setTitle(title);
                //图片 第一
                //[{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},{"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]

                List<Map> images = JSON.parseArray(goodsVo.getGoodsDesc().getItemImages(), Map.class);
                if(null != images && images.size() > 0){
                    item.setImage((String)images.get(0).get("url"));
                }
                //第三级商品分类的ID
                item.setCategoryid(goodsVo.getGoods().getCategory3Id());
                //第三级商品分类的名称
                item.setCategory(itemCatDao.selectByPrimaryKey(goodsVo.getGoods().getCategory3Id()).getName());
                //添加时间
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());

                //外键
                item.setGoodsId(goodsVo.getGoods().getId());
                //商家的ID
                item.setSellerId(goodsVo.getGoods().getSellerId());
                //商家的名称
                item.setSeller(sellerDao.selectByPrimaryKey(goodsVo.getGoods().getSellerId()).getNickName());

                //品牌名称
                item.setBrand(brandDao.selectByPrimaryKey(goodsVo.getGoods().getBrandId()).getName());

                //保存库存 表
                itemDao.insertSelective(item);
            }
        }
    }*/

    @Override
    public void updateStatus(Long[] ids, String status) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setStatus(status);
        for (Long id : ids) {
            seckillGoods.setId(id);
            seckillGoods.setCheckTime(new Date());
            seckillGoodsDao.updateByPrimaryKeySelective(seckillGoods);

        }

    }

   /* @Override
    public void delete(Long[] ids) {
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setIsDelete("1");
        for (Long id : ids) {
            seckillGoods.setId(id);
            seckillGoodsDao.updateByPrimaryKey(seckillGoods);
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(String.valueOf(id));
                }
            });

        }
    }*/
}
