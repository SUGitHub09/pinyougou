package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Brand;
import com.alibaba.dubbo.config.annotation.Service;
import entity.PageResult;
import entity.Result;

import java.util.List;
import java.util.Map;


public interface BrandService {

    List<Brand> findAll();

    PageResult findPage(Integer pageNo, Integer pageSize);

    void save(Brand brand);

    Brand findOne(Long id);

    void update(Brand brand);


    void delete(Long[] ids);


    PageResult findsearch(Integer pageNo, Integer pageSize, Brand brand);

    List<Map> selectOptionList();


    void updateStatus(Long[] ids, String status);

    void uploadExcelForStore(List<String[]> list);

}
