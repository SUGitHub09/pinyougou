package cn.itcast.core.service;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private BrandDao brandDao;
    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer pageNo, Integer pageSize) {
       PageHelper.startPage(pageNo, pageSize);
        Page<Brand> brands = (Page<Brand>) brandDao.selectByExample(null);
        return new PageResult(brands.getTotal(),brands.getResult());
    }

    @Override
    public void save(Brand brand) {
        brandDao.insertSelective(brand);
    }

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Brand brand) {

        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            brandDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findsearch(Integer pageNo, Integer pageSize, Brand brand) {
        PageHelper.startPage(pageNo, pageSize);
        //以id大小排序
        PageHelper.orderBy("id desc");
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
       /* //id 排序
        brandQuery.setOrderByClause("id desc");*/
        if(null!=brand){

            if (null != brand.getName() && !"".equals(brand.getName().trim())) {
                criteria.andNameLike("%" + brand.getName().trim() + "%");
            }
            if(null != brand.getFirstChar() && !"".equals(brand.getFirstChar().trim())){
                criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
            }
            if (brand.getStatus()!=null&& !brand.getStatus().trim().equals("")){
            criteria.andStatusEqualTo(brand.getStatus().trim());
            }

        }

        Page<Brand> brands = (Page<Brand>) brandDao.selectByExample(brandQuery);
        return new PageResult(brands.getTotal(),brands.getResult());
    }

    @Override
    public List<Map> selectOptionList() {

        return brandDao.selectOptionList();
    }

    //修改状态
    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            Brand brand = new Brand();
            brand.setId(id);
            brand.setStatus(status);
            brandDao.updateByPrimaryKeySelective(brand);
        }
	}

    @Override
    public void uploadExcelForStore(List<String[]> list) {

        if (list != null && list.size() > 0) {


            for (String[] strings : list) {
                Brand brand = new Brand();
                brand.setId(Long.parseLong(strings[0]));
                brand.setName(strings[1]);
                brand.setFirstChar(strings[2]);
                brandDao.insertSelective(brand);
            }

        }



    }
}
