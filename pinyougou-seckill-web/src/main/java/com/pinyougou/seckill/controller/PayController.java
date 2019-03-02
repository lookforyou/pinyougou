package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
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
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchSeckillOrderFromRedisByUserId(userId);
        if (tbSeckillOrder != null) {
            return weixinPayService.createNative(tbSeckillOrder.getId() + "", (long) (tbSeckillOrder.getMoney().doubleValue() * 100) + "");
        } else {
            return new HashMap();
        }
    }

    @RequestMapping("/queryPayStatus")
    public ResultInfo queryPayStatus(String out_trade_no) {
        ResultInfo resultInfo = null;
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        int count = 0;
        while (true) {
            //获取微信支付服务器返回回来的数据
            Map<String, String> map = weixinPayService.queryPayStatus(out_trade_no);
            if (map == null) {
                resultInfo = new ResultInfo(false, "支付异常");
                break;
            }
            if ("SUCCESS".equals(map.get("trade_state"))) {
                resultInfo = new ResultInfo(true, "支付成功");
                //保存订单
                seckillOrderService.saveSeckillOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                break;
            }
            //降低服务器压力
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            count++;
            //如果超过5分钟未支付就停止循环
            if (count >= 100) {
                resultInfo = new ResultInfo(false, "二维码过期");
                Map<String, String> closeOrder = weixinPayService.closeOrder(out_trade_no);
                if ("FAIL".equals(closeOrder.get("return_code"))) {
                    if ("ORDERPAID".equals(closeOrder.get("err_code"))) {
                        resultInfo = new ResultInfo(true, "支付成功");
                        //保存订单
                        seckillOrderService.saveSeckillOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), map.get("transaction_id"));
                    }
                }
                if (!resultInfo.isSuccess()) {
                    seckillOrderService.deleteSeckillOrderFromRedis(userId, Long.valueOf(out_trade_no));
                }
                break;
            }
        }
        return resultInfo;
    }
}
