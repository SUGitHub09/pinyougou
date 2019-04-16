package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;
import vo.GoodsVo;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
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
    @Override
    public void add(GoodsVo goodsVo) {
        goodsVo.getGoods().setAuditStatus("0");
        goodsDao.insertSelective(goodsVo.getGoods());
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
    }

    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        if (goods.getSellerId()==null){
            goods.setSellerId("qiandu");
        }
        PageHelper.startPage(page, rows);
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        criteria.andSellerIdEqualTo(goods.getSellerId());
            if (goods.getAuditStatus()!=null&&!"".equals(goods.getAuditStatus())){
               criteria.andAuditStatusEqualTo(goods.getAuditStatus()    );
            }if (goods.getGoodsName()!=null&&!"".equals(goods.getGoodsName().trim())){
            criteria.andGoodsNameLike("%"+goods.getGoodsName().trim()+"%");
            }
            if(goods.getId()!=null) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            criteria.andIsDeleteIsNull();
        Page<Goods> page1 =(Page<Goods>) goodsDao.selectByExample(goodsQuery);
        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo vo = new GoodsVo();
        Goods goods = goodsDao.selectByPrimaryKey(id);
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(itemQuery);
        vo.setGoods(goods);
        vo.setGoodsDesc(goodsDesc);
        vo.setItemList(items);
        return vo;
    }

    @Override
    public void update(GoodsVo goodsVo) {
        goodsVo.getGoods().setAuditStatus("0");
        goodsDao.updateByPrimaryKeySelective(goodsVo.getGoods());
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
    }
        @Autowired
        private SolrTemplate solrTemplate;
    @Override
    public void updateStatus(Long[] ids, String status) {
        Goods goods = new Goods();
        goods.setAuditStatus(status);
        for (Long id : ids) {
            goods.setId(id);
            goodsDao.updateByPrimaryKeySelective(goods);
            if ("1".equals(status)){

                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(String.valueOf(id));
                    }
                });
            }
        }

    }

    @Override
    public void delete(Long[] ids) {
        Goods goods = new Goods();
        goods.setIsDelete("1");
        for (Long id : ids) {
            goods.setId(id);
            goodsDao.updateByPrimaryKey(goods);
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(String.valueOf(id));
                }
            });

        }
    }
}