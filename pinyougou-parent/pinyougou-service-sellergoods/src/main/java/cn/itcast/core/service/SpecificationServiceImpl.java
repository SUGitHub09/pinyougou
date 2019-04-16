package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;

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
        //排序由大到小
        PageHelper.orderBy("id desc");
        //只要status不为null ,""就添加到条件中;

        if (specification!=null){
            if (specification.getSpecName()!=null&& !"".equals(specification.getSpecName().trim())){
                criteria.andSpecNameLike("%"+specification.getSpecName().trim()+"%");

            }
            if (specification.getStatus()!=null&& !specification.getStatus().trim().equals("")){
                criteria.andStatusEqualTo(specification.getStatus().trim());

            }
        }

        Page<Specification> specifications = (Page<Specification>) specificationDao.selectByExample(specificationQuery);
        return new PageResult(specifications.getTotal(),specifications.getResult());
    }

    @Override
    public void add(SpecificationVo specificationvo) {
        specificationDao.insertSelective(specificationvo.getSpecification());
        //获取规格状态
        String specName = specificationvo.getSpecification().getSpecName();
        SpecificationQuery specificationQuery = new SpecificationQuery();
        specificationQuery.createCriteria().andSpecNameEqualTo(specName);
        List<Specification> specifications = specificationDao.selectByExample(specificationQuery);
        Specification specification = specifications.get(0);
        Long id = specification.getId();

        if(specificationvo.getSpecificationOptionList()!=null){
            List<SpecificationOption> specificationOptions = specificationvo.getSpecificationOptionList();
            for (SpecificationOption specificationOption : specificationOptions) {
                specificationOption.setSpecId(id);
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
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            Specification specification = new Specification();
            specification.setId(id);
            specification.setStatus(status);
            specificationDao.updateByPrimaryKeySelective(specification);
        }
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
