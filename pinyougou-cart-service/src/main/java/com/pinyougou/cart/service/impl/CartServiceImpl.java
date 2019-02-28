package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.dao.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCarts(List<Cart> carts, Long itemId, Integer num) {
        //查询商品sku信息
        TbItem tbItem = tbItemMapper.selectByPrimaryKey(itemId);
        if (!"1".equals(tbItem.getStatus())) {
            throw new RuntimeException("商品状态不正常");
        }
        if (tbItem.getNum() == 0) {
            throw new RuntimeException("商品库存不足");
        }
        //根据sku信息查询商家id
        String sellerId = tbItem.getSellerId();
        //根据商家id查询购物车对象
        Cart cart = searchCartsBySellerId(carts, sellerId);
        //如果购物车中不存在商家的购物车
        if (cart == null) {
            //创建新的购物车对象添加到购物车集合中
            cart = new Cart();
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            List<TbOrderItem> tbOrderItems = new ArrayList<TbOrderItem>();
            TbOrderItem tbOrderItem = createOrderItem(tbItem, num);
            tbOrderItems.add(tbOrderItem);
            cart.setOrderItems(tbOrderItems);
            carts.add(cart);
        } else {
            List<TbOrderItem> orderItems = cart.getOrderItems();
            TbOrderItem tbOrderItem = searchOrderItemByItemId(orderItems, tbItem);
            if (tbOrderItem == null) {
                //如果存在购物车集合中，则判断购物车明细列表中是否存在，不存在就创建购物明细对象
                tbOrderItem = createOrderItem(tbItem, num);
                orderItems.add(tbOrderItem);
            } else {
                //如果购物车明细列表存在，则按商品原有的数量累加，并且计算总金额
                tbOrderItem.setNum(num + tbOrderItem.getNum());
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getPrice().doubleValue() * tbOrderItem.getNum()));
                if (tbOrderItem.getNum() <= 0) {
                    orderItems.remove(tbOrderItem);
                }
                if (orderItems.size() == 0) {
                    carts.remove(orderItems);
                }
            }
        }
        return carts;
    }

    @Override
    public List<Cart> findCartsByRedis(String username) {
        List<Cart> carts = null;
        try {
            carts = (List<Cart>) redisTemplate.boundHashOps("carts").get(username);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (carts == null) {
            carts = new ArrayList<>();
        }
        return carts;
    }

    @Override
    public void addCartsToRedis(String username, List<Cart> carts) {
        try {
            redisTemplate.boundHashOps("carts").put(username, carts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cart> mergeCarts(List<Cart> redisCarts, List<Cart> cookieCarts) {
        for (Cart cookieCart : cookieCarts) {
            for (TbOrderItem orderItem : cookieCart.getOrderItems()) {
                redisCarts = addGoodsToCarts(redisCarts, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCarts;
    }

    /**
     * 根据用户传入的购物车列表根据商家id查询购物车是否有匹配的购物车对象
     * @param carts 购物车列表
     * @param sellerId 商家id
     * @return 返回购物车对象
     */
    private Cart searchCartsBySellerId(List<Cart> carts, String sellerId) {
        for (Cart cart : carts) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }

    /**
     * 创建购物明细对象
     * @param tbItem
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem tbItem, Integer num) {
        TbOrderItem tbOrderItem = new TbOrderItem();
        tbOrderItem.setItemId(tbItem.getId());
        tbOrderItem.setGoodsId(tbItem.getGoodsId());
        tbOrderItem.setNum(num);
        tbOrderItem.setTitle(tbItem.getTitle());
        tbOrderItem.setPicPath(tbItem.getImage());
        tbOrderItem.setPrice(tbItem.getPrice());
        tbOrderItem.setSellerId(tbItem.getSellerId());
        tbOrderItem.setTotalFee(new BigDecimal(num * tbItem.getPrice().doubleValue()));
        return tbOrderItem;
    }

    /**
     * 根据tbItem对象查找购物明细列表是否有匹配的的购物明细对象
     * @param tbOrderItems
     * @param tbItem
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> tbOrderItems, TbItem tbItem) {
        for (TbOrderItem tbOrderItem : tbOrderItems) {
            if (tbOrderItem.getItemId().longValue() == tbItem.getId().longValue()) {
                return tbOrderItem;
            }
        }
        return null;
    }
}
