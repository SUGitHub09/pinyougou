package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService{
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> itemSearch(Map<String, String> searchMap) {
        Map<String, Object> map = new HashMap<>();
        List<String> categoryList = searchCategoryByKeywords(searchMap);
        map.put("categoryList",categoryList);
        if (null!=categoryList&&categoryList.size()>0){
            map.putAll(selectBrandAndTemplateByCategory(categoryList.get(0)));
        }
        //1.商品分类
        //2.品牌结果集
        //3.规格结果集
        //4.查询分页结果集
        map.putAll(itemSearchByPage(searchMap));
        return map;
    }
    public Map<String,Object> selectBrandAndTemplateByCategory(String category){
        HashMap<String, Object> map = new HashMap<>();
        Object itemcatList = redisTemplate.boundHashOps("itemcatList").get(category);
        List brandList = (List) redisTemplate.boundHashOps("brandList").get(itemcatList);
        List specList = (List) redisTemplate.boundHashOps("specList").get(itemcatList);
        map.put("brandList",brandList);
        map.put("specList",specList);
        return map;
    }
    public  List<String> searchCategoryByKeywords(Map<String, String> searchMap){
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        Query query = new SimpleQuery(criteria);
        List<String> categoryList = new ArrayList<>();
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<Item> page = solrTemplate.queryForGroupPage(query, Item.class);
        GroupResult<Item> item_category = page.getGroupResult("item_category");
        List<GroupEntry<Item>> content = item_category.getGroupEntries().getContent();
        if (null!=content){
            for (GroupEntry<Item> entry : content) {
                String value = entry.getGroupValue();
                categoryList.add(value);
            }
        }

        return categoryList;

    }
    public Map<String, Object> itemSearchByPage(Map<String, String> searchMap){
        Map<String, Object> map = new HashMap<>();
        searchMap.put("keywords",searchMap.get("keywords").replace(" ",""));
        //添加高亮域查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        HighlightQuery query=new SimpleHighlightQuery(criteria);
        //过滤条件
        //1.商品分类名称
        if (null!=searchMap.get("category")&&!"".equals(searchMap.get("category").trim())){
            FilterQuery fq=new SimpleFilterQuery(new Criteria("item_category").is(searchMap.get("category").trim()));
            query.addFilterQuery(fq);
        }
        //2.品牌
        if (null!=searchMap.get("brand")&&!"".equals(searchMap.get("brand").trim())){
            FilterQuery fq=new SimpleFilterQuery(new Criteria("item_brand").is(searchMap.get("brand").trim()));
            query.addFilterQuery(fq);
        }
        //3.规格(多个)
        if (null!=searchMap.get("spec")&&!"".equals(searchMap.get("spec").trim())){
            Map<String,String> spec = JSON.parseObject(searchMap.get("spec"), Map.class);
            Set<Map.Entry<String, String>> entries = spec.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                FilterQuery fq=new SimpleFilterQuery(new Criteria("item_spec_"+entry.getKey()).is(entry.getValue()));
                query.addFilterQuery(fq);
            }
        }
        //4.价格区间
        if (null!=searchMap.get("price")&&!"".equals(searchMap.get("price"))){
            String[] prices = searchMap.get("price").split("-");
            if (prices[1].equals("*")){
                FilterQuery fq=new SimpleFilterQuery(new Criteria("item_price").greaterThanEqual(prices[0]));
                query.addFilterQuery(fq);
            }else {
                FilterQuery fq=new SimpleFilterQuery(new Criteria("item_price").between(prices[0],prices[1]));
                query.addFilterQuery(fq);
            }
        }

        //1.综合排序
        //2.新品排序
        //3.价格由低到高
        //4.价格由高到低
        if (null!=searchMap.get("sort")&&!"".equals(searchMap.get("sort"))){
            if (searchMap.get("sort").equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC,"item_"+searchMap.get("sortField"));
                query.addSort(sort);
            }else {
                Sort sort = new Sort(Sort.Direction.DESC,"item_"+searchMap.get("sortField"));
                query.addSort(sort);
            }
        }


        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");
        query.setHighlightOptions(options);

        //分页
        String pageNo = searchMap.get("pageNo");
        String pageSize = searchMap.get("pageSize");
        query.setOffset((Integer.parseInt(pageNo)-1)*Integer.parseInt(pageSize));
        query.setRows(Integer.parseInt(pageSize));
        //创建高亮查询
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(query, Item.class);
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        //将高亮查询结果塞到显示的域中
        for (HighlightEntry<Item> highlightEntry : highlighted) {
            Item entity = highlightEntry.getEntity();
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            if (highlights!=null&&highlights.size()>0){
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }
        map.put("rows",items.getContent());
        map.put("total",items.getTotalElements());
        map.put("totalPages",items.getTotalPages());
        return map;
    }
}
