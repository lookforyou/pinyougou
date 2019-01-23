package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import entity.PageResult;

import java.util.List;

public interface BrandService {
    List<TbBrand> findAll();

    PageResult findByPage(Integer pageNum, Integer pageSize);

    void add(TbBrand brand);

    TbBrand findById(Long id);

    void update(TbBrand brand);

    void deleteBrands(Long... ids);

    PageResult findByPage(TbBrand brand, Integer pageNum, Integer pageSize);
}
