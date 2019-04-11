package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Goods;
import entity.PageResult;
import vo.GoodsVo;

public interface GoodsService {
    void add(GoodsVo goodsVo);

    PageResult search(Integer page, Integer rows, Goods goods);

    GoodsVo findOne(Long id);

    void update(GoodsVo goodsVo);

    void updateStatus(Long[] ids, String status);

    void delete(Long[] ids);
}
