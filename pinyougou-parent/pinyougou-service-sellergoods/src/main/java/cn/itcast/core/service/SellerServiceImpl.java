package cn.itcast.core.service;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private SellerDao sellerDao;
    @Override
    public void add(Seller seller) {
        seller.setPassword(new BCryptPasswordEncoder().encode(seller.getPassword()));
        seller.setStatus("0");
        sellerDao.insertSelective(seller);
    }

    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        PageHelper.startPage(page, rows);
        SellerQuery sellerQuery=new SellerQuery();
        SellerQuery.Criteria criteria = sellerQuery.createCriteria();
        if (seller!=null){
            criteria.andStatusEqualTo("0");
            if (seller.getName()!=null&& !"".equals(seller.getName().trim())){
                criteria.andNameLike("%"+seller.getName().trim()+"%");
            }
            if (seller.getNickName()!=null&& !seller.getNickName().trim().equals("")){
                criteria.andNickNameLike("%"+seller.getNickName().trim()+"%");
            }
        }
        Page<Seller> sellers = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
        return new PageResult(sellers.getTotal(),sellers.getResult());
    }

    @Override
    public Seller findOne(String id) {
        return  sellerDao.selectByPrimaryKey(id);
    }

    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }

    @Override
    public Seller findBySellerId(String username) {
        return sellerDao.selectByPrimaryKey(username);
    }
}
