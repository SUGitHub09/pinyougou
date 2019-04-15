package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import vo.SpecificationVo;

import java.util.List;
import java.util.Map;

@Service
public class SpecificationServiceImpl implements SpecificationService{
    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        PageHelper.startPage(page, rows);
        SpecificationQuery specificationQuery = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();
        if (specification!=null){
            if (specification.getSpecName()!=null&& !"".equals(specification.getSpecName().trim())){
                criteria.andSpecNameEqualTo(specification.getSpecName().trim());
            }
        }
        Page<Specification> specifications = (Page<Specification>) specificationDao.selectByExample(specificationQuery);
        return new PageResult(specifications.getTotal(),specifications.getResult());
    }

    @Override
    public void add(SpecificationVo specificationvo) {
        specificationDao.insertSelective(specificationvo.getSpecification());
        if(specificationvo.getSpecificationOptionList()!=null){
            List<SpecificationOption> specificationOptions = specificationvo.getSpecificationOptionList();
            for (SpecificationOption specificationOption : specificationOptions) {
                specificationOption.setSpecId(specificationvo.getSpecification().getId());
                specificationOptionDao.insertSelective(specificationOption);
            }
        }
    }

    @Override
    public SpecificationVo findOne(Long id) {
        SpecificationVo specificationVo = new SpecificationVo();
        specificationVo.setSpecification(specificationDao.selectByPrimaryKey(id));
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);
        specificationVo.setSpecificationOptionList(specificationOptions);
        return specificationVo;
    }

    @Override
    public void update(SpecificationVo specificationvo) {
        specificationDao.updateByPrimaryKeySelective(specificationvo.getSpecification());

        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(specificationvo.getSpecification().getId());
        specificationOptionDao.deleteByExample(query);

        List<SpecificationOption> list = specificationvo.getSpecificationOptionList();
        for (SpecificationOption option : list) {
            option.setSpecId(specificationvo.getSpecification().getId());
            specificationOptionDao.insertSelective(option);
        }

    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            specificationDao.deleteByPrimaryKey(id);
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(query);
        }

    }

    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }

    @Override
    public void uploadExcelForStore(List<String[]> list) {

        if (list != null && list.size() > 0) {

            for (String[] strings : list) {

                Specification specification = new Specification();
                specification.setId(Long.parseLong(strings[0]));
                specification.setSpecName(strings[1]);

                specificationDao.insertSelective(specification);



            }





        }


    }
}
