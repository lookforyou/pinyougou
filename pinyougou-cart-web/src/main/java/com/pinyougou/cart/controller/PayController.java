package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import util.IdWorker;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;

    @RequestMapping("/createNative")
    public Map createNative(String out_trade_no, String total_fee) {
        IdWorker idWorker = new IdWorker();
        return weixinPayService.createNative(idWorker.nextId() + "", total_fee);
    }
}
