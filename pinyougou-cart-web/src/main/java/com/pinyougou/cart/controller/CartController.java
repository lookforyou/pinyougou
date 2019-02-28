package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;
import entity.ResultInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;

    @RequestMapping("/findCarts")
    public List<Cart> findCarts() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("登录账户名称：" + name);
        String carts_cookie = CookieUtil.getCookieValue(request, "carts", "UTF-8");
        if (carts_cookie == null || "".equals(carts_cookie)) {
            carts_cookie = "[]";
        }
        List<Cart> cookieCarts = JSON.parseArray(carts_cookie, Cart.class);
        if ("anonymousUser".equals(name)) {
            return cookieCarts;
        } else {
            List<Cart> redisCarts = cartService.findCartsByRedis(name);
            if (cookieCarts.size() > 0) {
                List<Cart> mergeCarts = cartService.mergeCarts(redisCarts, cookieCarts);
                cartService.addCartsToRedis(name, mergeCarts);
                CookieUtil.deleteCookie(request, response, "carts");
                return mergeCarts;
            }
            return redisCarts;
        }
    }

    @RequestMapping("/addGoodsToCarts")
    @CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")
    public ResultInfo addGoodsToCarts(Long itemId, Integer num) {
        try {
            //设置允许跨域请求
//            response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
            //跨域允许使用cookie
//            response.setHeader("Access-Control-Allow-Credentials", "true");
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //取出购物车
            List<Cart> carts = findCarts();
            //调用服务层方法构建购物车
            carts = cartService.addGoodsToCarts(carts, itemId, num);
            if ("anonymousUser".equals(name)) {
                //返回cookie
                String carts_json = JSON.toJSONString(carts);
                CookieUtil.setCookie(request, response, "carts", carts_json, 3600 * 24, "UTF-8");
            } else {
                //如果用户登录把购物车存入redis
                cartService.addCartsToRedis(name, carts);
            }
            return new ResultInfo(true, "存入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "存入购物车失败");
        }
    }
}
