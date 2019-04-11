package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;

import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.Cart;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;
    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9003")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response){
        try {
            List<Cart> cartList=null;
            //1.未登录  获取cookie
            Cookie[] cookies = request.getCookies();
            if (null!=cookies&&cookies.length>0){
                for (Cookie cookie : cookies) {
                    //2.获取cookie中购物车集合
                    if ("CART".equals(cookie.getName())){
                        String value = cookie.getValue();
                        cartList = JSON.parseArray(URLDecoder.decode(value,"UTF-8"), Cart.class);
                        break;
                    }
                }
            }
            //3.没有创建购物车集合
                if (null==cartList){
                    cartList= new ArrayList<>();
                }
            //4.追加当前款
            //查询订单
            Item item= cartService.findItem(itemId);
            //创建新购物车
            Cart newCart = new Cart();
            newCart.setSellerId(item.getSellerId());
            //创建新商品
            OrderItem newOrderItem = new OrderItem();
            newOrderItem.setItemId(itemId);
            newOrderItem.setNum(num);
            //创建新商品集合
            List<OrderItem> newOrderItemList=new ArrayList<>();
            newOrderItemList.add(newOrderItem);
            newCart.setOrderItemList(newOrderItemList);
            //判断新购物车是否存在于老购物车集合中
            int newIndexof = cartList.indexOf(newCart);
            if (newIndexof!=-1){
                //若存在,找到相同商家对应的购物车,并获取所有商品集合
                Cart oldCart = cartList.get(newIndexof);
                List<OrderItem> orderItemList = oldCart.getOrderItemList();
                //判断新商品是否存在于商品集合中
                int indexOf = orderItemList.indexOf(newOrderItem);
                if(indexOf!=-1){
                    //若存在
                    OrderItem orderItem = orderItemList.get(indexOf);
                    orderItem.setNum(orderItem.getNum()+newOrderItem.getNum());
                }else {
                    //若不存在
                    orderItemList.add(newOrderItem);
                }
            }else {
                //若不存在
                //添加新购物车
                cartList.add(newCart);
            }
            String name= SecurityContextHolder.getContext().getAuthentication().getName();
            if (!"anonymousUser".equals(name)){
                //已登录
                    cartService.addCartToRedis(cartList,name);
                Cookie cookie = new Cookie("CART", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);

            }else {
                //未登录
                //5.创建cookie 保存购物车 并回显浏览器
                Cookie cookie = new Cookie("CART", URLEncoder.encode(JSON.toJSONString(cartList),"UTF-8"));
                cookie.setMaxAge(60*60*24*7);
                cookie.setPath("/");
                response.addCookie(cookie);
            }


            return new Result(true,"加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"加入购物车失败");
        }
    }
    @RequestMapping("findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){
        List<Cart> cartList=null;
        //1.未登录  获取cookie
        Cookie[] cookies = request.getCookies();
        if (null!=cookies&&cookies.length>0){
            for (Cookie cookie : cookies) {
                //2.获取cookie中购物车集合
                if ("CART".equals(cookie.getName())){
                    String value = cookie.getValue();
                    try {
                        cartList = JSON.parseArray(URLDecoder.decode(value,"UTF-8"), Cart.class);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        String name= SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(name)){
            if (null!=cartList){
                cartService.addCartToRedis(cartList,name);
                Cookie cookie = new Cookie("CART", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            cartList=cartService.findFromRedis(name);
        }
        if (cartList!=null){
            cartList= cartService.findCartList(cartList);
        }
            return cartList;

    }
}
