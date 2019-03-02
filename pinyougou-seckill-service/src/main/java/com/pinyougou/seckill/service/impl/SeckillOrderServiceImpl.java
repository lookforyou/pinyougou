package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.dao.TbSeckillGoodsMapper;
import com.pinyougou.dao.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeckillOrderExample;
import com.pinyougou.pojo.TbSeckillOrderExample.Criteria;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import util.IdWorker;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillOrder> findAll() {
        return seckillOrderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillOrderExample example = new TbSeckillOrderExample();
        Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
            }
            if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
            }
            if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
            }
            if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
                criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
            }
            if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
            }
            if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
            }
            if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
                criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
            }

        }

        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void submitOrder(Long seckillGoodId, String userId) {
        TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillGoodId);
        if (seckillGoods == null) {
            throw new RuntimeException("商品不存在");
        }
        if (seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("商品库存不足");
        }
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        redisTemplate.boundHashOps("seckillGoods").put(seckillGoodId, seckillGoods);
        if (seckillGoods.getStockCount() == 0) {
            seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            redisTemplate.boundHashOps("seckillGoods").delete(seckillGoodId);
        }
        TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
        tbSeckillOrder.setId(idWorker.nextId());
        tbSeckillOrder.setSeckillId(seckillGoods.getId());
        tbSeckillOrder.setMoney(seckillGoods.getCostPrice());
        tbSeckillOrder.setUserId(userId);
        tbSeckillOrder.setSellerId(seckillGoods.getSellerId());
        tbSeckillOrder.setCreateTime(new Date());
        tbSeckillOrder.setStatus("0");
        redisTemplate.boundHashOps("seckillOrder").put(userId, tbSeckillOrder);
    }

    @Override
    public TbSeckillOrder searchSeckillOrderFromRedisByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }

    @Override
    public void saveSeckillOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        TbSeckillOrder tbSeckillOrder = searchSeckillOrderFromRedisByUserId(userId);
        if (tbSeckillOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        if (tbSeckillOrder.getId().longValue() != orderId.longValue()) {
            throw new RuntimeException("订单号不正确");
        }
        tbSeckillOrder.setStatus("1");
        tbSeckillOrder.setTransactionId(transactionId);
        tbSeckillOrder.setPayTime(new Date());

        seckillOrderMapper.insert(tbSeckillOrder);
        redisTemplate.boundHashOps("seckillOrder").delete(userId);
    }

    @Override
    public void deleteSeckillOrderFromRedis(String userId, Long orderId) {
        TbSeckillOrder tbSeckillOrder = searchSeckillOrderFromRedisByUserId(userId);
        if (tbSeckillOrder != null) {
            redisTemplate.boundHashOps("seckillOrder").delete(userId);
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(tbSeckillOrder.getSeckillId());
            if (seckillGoods != null) {
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
                redisTemplate.boundHashOps("seckillGoods").put(tbSeckillOrder.getSeckillId(), seckillGoods);
            } else {
                seckillGoods = new TbSeckillGoods();
                seckillGoods.setId(tbSeckillOrder.getSeckillId());
                seckillGoods.setStockCount(1);
                redisTemplate.boundHashOps("seckillGoods").put(tbSeckillOrder.getSeckillId(), seckillGoods);
            }
        }
    }

}
