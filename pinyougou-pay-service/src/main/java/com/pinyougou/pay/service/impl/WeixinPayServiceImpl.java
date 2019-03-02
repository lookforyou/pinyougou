package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import util.HttpClient;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${notifyurl}")
    private String notifyurl;

    @Value("${partnerkey}")
    private String partnerkey;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("appid", appid);
        data.put("mch_id", partner);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("body", "品优购");
        data.put("out_trade_no", out_trade_no);
        data.put("total_fee", total_fee);
        data.put("spbill_create_ip", "127.0.0.1");
        data.put("notify_url", notifyurl);
        data.put("trade_type", "NATIVE");
        try {
            String dataXml = WXPayUtil.generateSignedXml(data, partnerkey);
            System.out.println("Xml内容：" + dataXml);
            //发送请求到WeiXin
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setXmlParam(dataXml);
            httpClient.setHttps(true);
            httpClient.post();
            String result = httpClient.getContent();
            //将返回结果字符串转换为Map对象
            Map<String, String> xmlToMap = WXPayUtil.xmlToMap(result);
            System.out.println("返回结果为：" + xmlToMap);
            Map<String, String> map = new HashMap<String, String>();
            //将结果Map重新封装
            map.put("code_url", xmlToMap.get("code_url"));
            map.put("out_trade_no", out_trade_no);
            map.put("total_fee", total_fee);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();
        }
    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("appid", appid);
        data.put("mch_id", partner);
        data.put("out_trade_no", out_trade_no);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String dataXml = WXPayUtil.generateSignedXml(data, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setXmlParam(dataXml);
            httpClient.setHttps(true);
            httpClient.post();
            String resultStr = httpClient.getContent();
            System.out.println("查询订单返回的结果：" + resultStr);
            return WXPayUtil.xmlToMap(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map closeOrder(String out_trade_no) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("appid", appid);
        data.put("mch_id", partner);
        data.put("out_trade_no", out_trade_no);
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String dataXml = WXPayUtil.mapToXml(data);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(dataXml);
            httpClient.post();
            String resultStr = httpClient.getContent();
            return WXPayUtil.xmlToMap(resultStr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
