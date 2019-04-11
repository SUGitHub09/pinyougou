package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemCatDao itemCatDao;
    public void index(Long id){
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        String path=getPath("/"+id+".html");
        Writer out=null;
        try {
            Template template = configuration.getTemplate("item.ftl");
            Map<String,Object> root =new HashMap<>();
            ItemQuery query = new ItemQuery();
            query.createCriteria().andGoodsIdEqualTo(id);
            List<Item> items = itemDao.selectByExample(query);
            root.put("itemList",items);
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
            root.put("goodsDesc",goodsDesc);
            Goods goods = goodsDao.selectByPrimaryKey(id);
            root.put("goods",goods);
            String itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName();
            root.put("itemCat1",itemCat1);
            root.put("itemCat2",itemCat2);
            root.put("itemCat3",itemCat3);
            out=new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
            template.process(root,out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (null!=out){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //获取全路径
    public  String getPath(String path){
        return servletContext.getRealPath(path);
    }
    private ServletContext servletContext;
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;
    }
}
