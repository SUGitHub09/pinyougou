package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import vo.Cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private ItemDao itemDao;

    @Override
    public Item findItem(Long itemId) {
        return itemDao.selectByPrimaryKey(itemId);
    }

    @Override
    public List<Cart> findCartList(List<Cart> cartList) {
        for (Cart cart : cartList) {
            //商家名称
            Item item = null;
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                //图片
                item = findItem(orderItem.getItemId());
                orderItem.setPicPath(item.getImage());
                //标题
                orderItem.setTitle(item.getTitle());
                //单价
                orderItem.setPrice(item.getPrice());
                //小计
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
            }
            cart.setSellerName(item.getSeller());
        }
        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void addCartToRedis(List<Cart> newCartList, String name) {
        //1.从缓存中获取原来的购物车
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
        //2.将新购物车与老购物车合并
        oldCartList = merge(oldCartList, newCartList);
        //3.将合并后的老车保存到缓存中
        redisTemplate.boundHashOps("CART").put(name, oldCartList);
    }

    @Override
    public List<Cart> findFromRedis(String name) {
        return (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
    }

    public List<Cart> merge(List<Cart> oldCartList, List<Cart> newCartList) {
        if (null != oldCartList && oldCartList.size() > 0) {
            if (null!=newCartList&&newCartList.size()>0){
                for (Cart newCart : newCartList) {
                    //判断新购物车是否存在于老购物车集合中
                    int newIndexof = oldCartList.indexOf(newCart);
                    if (newIndexof!=-1){
                        //若存在,找到相同商家对应的购物车,并获取所有商品集合
                        Cart oldCart = oldCartList.get(newIndexof);
                        List<OrderItem> orderItemList = oldCart.getOrderItemList();
                        //判断新商品是否存在于商品集合中
                        List<OrderItem> newOrderItemList = newCart.getOrderItemList();
                        for (OrderItem newOrderItem : newOrderItemList) {

                            int indexOf = orderItemList.indexOf(newOrderItem);
                            if(indexOf!=-1){
                                //若存在
                                OrderItem orderItem = orderItemList.get(indexOf);
                                orderItem.setNum(orderItem.getNum()+newOrderItem.getNum());
                            }else {
                                //若不存在
                                orderItemList.add(newOrderItem);
                            }
                        }
                    }else {
                        //若不存在
                        //添加新购物车
                        oldCartList.add(newCart);
                    }
                }
            }
        } else {
            return newCartList;
        }
        return oldCartList;
    }
}
