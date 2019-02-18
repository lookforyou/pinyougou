package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.dao.*;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.pojogroup.Goods;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbBrandMapper brandMapper;

    @Autowired
    private TbSellerMapper sellerMapper;

    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        TbGoods tbGoods = goods.getGoods();
        tbGoods.setAuditStatus("0");//设置商品未审核
        tbGoods.setIsMarketable("1");
        goodsMapper.insert(tbGoods);
        TbGoodsDesc goodsDesc = goods.getGoodsDesc();
        goodsDesc.setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insert(goodsDesc);
        saveItem(goods);
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(goods.getGoods().getId());
        //根据goodsId删除item里面原来的数据
        itemMapper.deleteByExample(example);
        //在添加新的数据
        saveItem(goods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdEqualTo(id);
        List<TbItem> items = itemMapper.selectByExample(example);
        goods.setItems(items);
        return goods;
    }

    /**
     * 批量逻辑删除
     */
    @Override
    public void delete(Long[] ids) {
        TbGoodsExample example = new TbGoodsExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        List<TbGoods> tbGoods = goodsMapper.selectByExample(example);
        for (TbGoods tbGood : tbGoods) {
            tbGood.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(tbGood);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();
        //指定条件为没有逻辑删除的数据
        criteria.andIsDeleteIsNull();
        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }
        }
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(String status, Long... ids) {
        TbGoodsExample example = new TbGoodsExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        List<TbGoods> tbGoods = goodsMapper.selectByExample(example);
        for (TbGoods tbGood : tbGoods) {
            tbGood.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(tbGood);
        }
    }

    @Override
    public void updateMarketTable(String status, Long... ids) {
        TbGoodsExample example = new TbGoodsExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        List<TbGoods> tbGoods = goodsMapper.selectByExample(example);
        for (TbGoods tbGood : tbGoods) {
            tbGood.setIsMarketable(status);
            goodsMapper.updateByPrimaryKey(tbGood);
        }
    }

    /**
     * 根据goodIds添加sku信息到索引库中
     * @param goodIds
     * @param status
     * @return
     */
    @Override
    public List<TbItem> findItemByGoodIdsAndStatus(Long[] goodIds, String status) {
        TbItemExample example = new TbItemExample();
        example.createCriteria().andGoodsIdIn(Arrays.asList(goodIds)).andStatusEqualTo(status);
        return itemMapper.selectByExample(example);
    }

    private void insertItem(Goods goods, TbItem item) {
        item.setCategoryid(goods.getGoods().getCategory3Id());
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setGoodsId(goods.getGoods().getId());
        item.setSellerId(goods.getGoods().getSellerId());
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());
        //获取图片JSON字符串并转换为List集合
        List<Map> list = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (list.size() > 0) {
            item.setImage((String) list.get(0).get("url"));
        }
    }

    private void saveItem(Goods goods) {
        List<TbItem> items = goods.getItems();
        //如果启用规格
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            for (TbItem item : items) {
                StringBuilder title = new StringBuilder(goods.getGoods().getGoodsName());
                Map<String, Object> map = JSON.parseObject(item.getSpec());
                for (String key : map.keySet()) {
                    title.append(" ").append(map.get(key));
                }
                item.setTitle(title.toString());
                insertItem(goods, item);
                itemMapper.insert(item);
            }
        } else {
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());
            item.setNum(9999);
            item.setStatus("0");
            item.setIsDefault("1");
            item.setSpec("{}");
            insertItem(goods, item);
            itemMapper.insert(item);
        }
    }
}
