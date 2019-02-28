package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List;

public interface CartService {
    List<Cart> addGoodsToCarts(List<Cart> carts, Long itemId, Integer num);

    List<Cart> findCartsByRedis(String username);

    void addCartsToRedis(String username, List<Cart> carts);

    List<Cart> mergeCarts(List<Cart> redisCarts, List<Cart> cookieCarts);
}
