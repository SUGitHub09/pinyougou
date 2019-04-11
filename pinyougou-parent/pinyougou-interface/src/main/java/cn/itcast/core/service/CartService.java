package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import vo.Cart;

import java.util.List;

public interface CartService {
    Item findItem(Long itemId);

    List<Cart> findCartList(List<Cart> cartList);

    void addCartToRedis(List<Cart> cartList, String name);

    List<Cart> findFromRedis(String name);
}
