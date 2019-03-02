package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeckillGoods> findAll() {
        return seckillGoodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return seckillGoodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param seckillGoods
     * @return
     */
    @RequestMapping("/add")
    public ResultInfo add(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.add(seckillGoods);
            return new ResultInfo(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param seckillGoods
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody TbSeckillGoods seckillGoods) {
        try {
            seckillGoodsService.update(seckillGoods);
            return new ResultInfo(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbSeckillGoods findOne(Long id) {
        return seckillGoodsService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public ResultInfo delete(Long[] ids) {
        try {
            seckillGoodsService.delete(ids);
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param seckillGoods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbSeckillGoods seckillGoods, int page, int rows) {
        return seckillGoodsService.findPage(seckillGoods, page, rows);
    }

    @RequestMapping("/findSeckillGoods")
    public List<TbSeckillGoods> findSeckillGoods() {
        return seckillGoodsService.findSeckillGoods();
    }

    @RequestMapping("/findSeckillGoodFromRedis")
    public TbSeckillGoods findSeckillGoodFromRedis(Long id) {
        return seckillGoodsService.findSeckillGoodFromRedis(id);
    }
}
