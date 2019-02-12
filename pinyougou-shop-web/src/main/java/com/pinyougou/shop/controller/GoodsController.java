package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findByPage")
    public PageResult findPage(int page, int rows) {
        return goodsService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param goods
     * @return
     */
    @RequestMapping("/add")
    public ResultInfo add(@RequestBody Goods goods) {
        //设置商家id
        goods.getGoods().setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
        try {
            goodsService.add(goods);
            return new ResultInfo(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody Goods goods) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        Goods goods1 = goodsService.findOne(goods.getGoods().getId());
        if (goods1.getGoods().getSellerId().equals(sellerId)) {
            try {
                goodsService.update(goods);
                return new ResultInfo(true, "修改成功");
            } catch (Exception e) {
                e.printStackTrace();
                return new ResultInfo(false, "修改失败");
            }
        }
        return new ResultInfo(false, "非法操作");
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findById")
    public Goods findOne(Long id) {
        return goodsService.findOne(id);
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
            goodsService.delete(ids);
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param goods
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbGoods goods, int page, int rows) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.findPage(goods, page, rows);
    }

    @RequestMapping("/updateMarketTable")
    public ResultInfo updateMarketTable(String status, Long... ids) {
        try {
            goodsService.updateMarketTable(status, ids);
            return new ResultInfo(true, "上架成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "上架失败");
        }
    }

}
