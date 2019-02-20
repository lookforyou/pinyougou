package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;

    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;

    @Reference(timeout = 10000)
    private ItemPageService itemPageService;

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
     * 修改
     *
     * @param goods
     * @return
     */
    @RequestMapping("/update")
    public ResultInfo update(@RequestBody Goods goods) {
        try {
            goodsService.update(goods);
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
            itemSearchService.deleteByIds(ids);
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
        return goodsService.findPage(goods, page, rows);
    }

    @RequestMapping("/updateStatus")
    public ResultInfo updateStatus(String status, Long... ids) {
        try {
            goodsService.updateStatus(status, ids);
            if ("1".equals(status)) {
                List<TbItem> tbItemList = goodsService.findItemByGoodIdsAndStatus(ids, status);
                itemSearchService.importList(tbItemList);
                for (Long id : ids) {
                    itemPageService.genItemHtml(id);
                }
            }
            return new ResultInfo(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "失败");
        }
    }

    /*@RequestMapping("/genItemHtml")
    public void genItemHtml(Long goodsId) {
        itemPageService.genItemHtml(goodsId);
    }*/

}
