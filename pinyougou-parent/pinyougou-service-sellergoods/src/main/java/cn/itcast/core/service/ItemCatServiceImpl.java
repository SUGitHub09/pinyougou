package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        List<ItemCat> list = itemCatDao.selectByExample(null);
        for (ItemCat itemCat : list) {
            redisTemplate.boundHashOps("itemcatList").put(itemCat.getName(),itemCat.getTypeId());
        }

        ItemCatQuery query = new ItemCatQuery();
        query.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(query);
    }

    @Override
    public void add(ItemCat itemCat) {
        itemCatDao.insertSelective(itemCat);
    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    @Override
    public void uploadExcelForStore(List<String[]> list) {
        if (list != null && list.size() > 0) {


            for (String[] strings : list) {
                ItemCat itemCat = new ItemCat();

                itemCat.setId(Long.parseLong(strings[0]));
                itemCat.setParentId(Long.parseLong(strings[1]));
                itemCat.setName(strings[2]);
                itemCat.setTypeId(Long.parseLong(strings[1]));
                itemCatDao.insertSelective(itemCat);


            }

        }
    }


}
