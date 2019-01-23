package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.dao.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    @Override
    public PageResult findByPage(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbBrand> brands = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        return new PageResult(brands.getTotal(), brands.getResult());
    }

    @Override
    public void add(TbBrand brand) {
        tbBrandMapper.insert(brand);
    }

    @Override
    public TbBrand findById(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand brand) {
        tbBrandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void deleteBrands(Long... ids) {
        TbBrandExample example = new TbBrandExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        tbBrandMapper.deleteByExample(example);
    }

    @Override
    public PageResult findByPage(TbBrand brand, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbBrandExample example = new TbBrandExample();
        if (brand != null) {
            if (StringUtils.isNotEmpty(brand.getName())) {
                example.createCriteria().andNameLike("%" + brand.getName() + "%");
            }
            if (StringUtils.isNotEmpty(brand.getFirstChar())) {
                example.createCriteria().andFirstCharLike("%" + brand.getFirstChar() + "%");
            }
        }
        Page<TbBrand> brands = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        return new PageResult(brands.getTotal(), brands.getResult());
    }
}
