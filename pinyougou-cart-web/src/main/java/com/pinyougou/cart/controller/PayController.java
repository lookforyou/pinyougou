package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import entity.ResultInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeixinPayService weixinPayService;

    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog tbPayLog = orderService.searchPayLogFormRedis(userId);
        if (tbPayLog != null) {
            return weixinPayService.createNative(tbPayLog.getOutTradeNo(), tbPayLog.getTotalFee() + "");
        } else {
            return new HashMap();
        }
    }

    @RequestMapping("/queryPayStatus")
    public ResultInfo queryPayStatus(String out_trade_no) {
        ResultInfo resultInfo = null;
        int count = 0;
        while (true) {
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                resultInfo = new ResultInfo(false, "支付异常");
                break;
            }
            if ("SUCCESS".equals(map.get("trade_state"))) {
                resultInfo = new ResultInfo(true, "支付成功");
                //支付成功修改payLog表中的数据
                orderService.updateOrderStatus(out_trade_no, map.get("transaction_id"));
                break;
            }
            //降低服务器压力
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            //如果超过15分钟未支付就停止循环
            if (count >= 100) {
                resultInfo = new ResultInfo(false, "二维码过期");
                break;
            }
        }
        return resultInfo;
    }
}
