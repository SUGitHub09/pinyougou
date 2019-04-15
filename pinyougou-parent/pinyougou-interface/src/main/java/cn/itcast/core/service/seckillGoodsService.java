package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import entity.PageResult;
import vo.GoodsVo;

public interface seckillGoodsService {
    /*void add(GoodsVo goodsVo);*/

    PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods);

    /*GoodsVo findOne(Long id);

    void update(GoodsVo goodsVo);*/

    void updateStatus(Long[] ids, String status);

    /*void delete(Long[] ids);*/
}
