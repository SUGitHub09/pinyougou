package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;

import cn.itcast.core.pojo.specification.Specification;

import cn.itcast.core.pojo.good.Brand;

import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {
    @Autowired
    TypeTemplateDao typeTemplateDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        for (TypeTemplate template : typeTemplates) {
            JSONArray brandList = JSON.parseArray(template.getBrandIds());
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
            List<Map> specList = findBySpecList(template.getId());
            redisTemplate.boundHashOps("specList").put(template.getId(),specList);
        }
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = typeTemplateQuery.createCriteria();
        if (typeTemplate!=null){
            if (typeTemplate.getName()!=null&& !"".equals(typeTemplate.getName().trim())){
                criteria.andNameLike("%"+typeTemplate.getName().trim()+"%");
            }
            if (typeTemplate.getStatus()!=null&& !typeTemplate.getStatus().trim().equals("")){
                criteria.andStatusEqualTo(typeTemplate.getStatus().trim());
            }
        }

        PageHelper.startPage(page,rows);
         Page page1= (Page) typeTemplateDao.selectByExample(typeTemplateQuery);
        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(TypeTemplate typeTemplate) {
         typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            typeTemplateDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        String specIds = typeTemplate.getSpecIds();
        List<Map> list = JSON.parseArray(specIds, Map.class);
        for (Map map : list) {
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo(Long.parseLong(String.valueOf(map.get("id"))));
            List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(query);
            map.put("options",specificationOptions);

        }
        return list;
    }

    @Override
    public void updateStatus(Long[] ids, String status) {

        for (Long id : ids) {
            TypeTemplate typeTemplate = new TypeTemplate();
            typeTemplate.setId(id);
            typeTemplate.setStatus(status);
            typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
		}
	}
	
	@Override
    public void uploadExcelForStore(List<String[]> list) {
        if (list != null && list.size() > 0) {


            for (String[] strings : list) {
                TypeTemplate typeTemplate = new TypeTemplate();

                typeTemplate.setId(Long.parseLong(strings[0]));
                typeTemplate.setName(strings[1]);
                typeTemplate.setSpecIds(strings[2]);
                typeTemplate.setBrandIds(strings[3]);
                typeTemplate.setCustomAttributeItems(strings[4]);
                typeTemplateDao.insertSelective(typeTemplate);

            }


        }
    }
}
