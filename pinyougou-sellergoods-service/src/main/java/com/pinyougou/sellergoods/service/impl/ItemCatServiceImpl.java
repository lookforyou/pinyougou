package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.dao.TbItemCatMapper;
import com.pinyougou.dao.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.pojo.TbItemCatExample.Criteria;
import com.pinyougou.pojogroup.ItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbTypeTemplateMapper tbTypeTemplateMapper;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;
    /**
     * 查询全部
     */
    @Override
    public List<TbItemCat> findAll() {
        return itemCatMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbItemCat itemCat) {
        itemCatMapper.insert(itemCat);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbItemCat itemCat) {
        itemCatMapper.updateByPrimaryKey(itemCat);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public ItemCat findOne(Long id) {
        ItemCat itemCat = new ItemCat();
        TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(id);
        List<Map> tbTypeTemplate = tbTypeTemplateMapper.findName(tbItemCat.getTypeId());
        itemCat.setTbItemCat(tbItemCat);
        itemCat.setTbTypeTemplate(tbTypeTemplate);
        return itemCat;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) throws Exception {
        TbItemCatExample exampleSelect = new TbItemCatExample();
        exampleSelect.createCriteria().andParentIdIn(Arrays.asList(ids));
        List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(exampleSelect);
        if (!tbItemCats.isEmpty()) {
            throw new Exception();
        }
        TbItemCatExample exampleDel = new TbItemCatExample();
        exampleDel.createCriteria().andIdIn(Arrays.asList(ids));
        itemCatMapper.deleteByExample(exampleDel);
    }


    @Override
    public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbItemCatExample example = new TbItemCatExample();
        Criteria criteria = example.createCriteria();
        if (itemCat != null) {
            if (itemCat.getName() != null && itemCat.getName().length() > 0) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }
        }
        Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        TbItemCatExample example = new TbItemCatExample();
        example.createCriteria().andParentIdEqualTo(parentId);
        saveDataToRedis();
        return itemCatMapper.selectByExample(example);
    }

    /**
     * 将模板名称和模板ID存入缓冲
     */
    private void saveDataToRedis() {
        List<TbItemCat> tbItemCats = findAll();
        for (TbItemCat tbItemCat : tbItemCats) {
            redisTemplate.boundHashOps("itemCat").put(tbItemCat.getName(), tbItemCat.getTypeId());
        }
        System.out.println("缓存分类名称和模板ID");
    }

}
