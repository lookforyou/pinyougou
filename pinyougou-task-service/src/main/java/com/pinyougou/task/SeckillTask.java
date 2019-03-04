package com.pinyougou.task;

import com.pinyougou.dao.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Scheduled(cron = "0/5 * * * * ?")
    public void refreshSeckillGoods() {
        List<Long> ids = new ArrayList<Long>(redisTemplate.boundHashOps("seckillGoods").keys());
        TbSeckillGoodsExample tbSeckillGoodsExample = new TbSeckillGoodsExample();
        tbSeckillGoodsExample.createCriteria().andStockCountGreaterThan(0).andStatusEqualTo("1").andStartTimeLessThanOrEqualTo(new Date()).andEndTimeGreaterThanOrEqualTo(new Date());
        if (ids.size() > 0) {
            tbSeckillGoodsExample.createCriteria().andIdNotIn(ids);
        }
        List<TbSeckillGoods> tbSeckillGoods = seckillGoodsMapper.selectByExample(tbSeckillGoodsExample);
        for (TbSeckillGoods seckillGood : tbSeckillGoods) {
            redisTemplate.boundHashOps("seckillGoods").put(seckillGood.getId(), seckillGood);
        }
    }

    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods() {
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods seckillGood : seckillGoods) {
            if (seckillGood.getEndTime().getTime() < new Date().getTime()) {
                seckillGoodsMapper.updateByPrimaryKey(seckillGood);
                redisTemplate.boundHashOps("seckillGoods").delete(seckillGood.getId());
            }
        }
    }
}
