package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import entity.PageResult;
import entity.ResultInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    @RequestMapping("/findByPage")
    public PageResult findByPage(@RequestParam(value = "pageNum", required = true, defaultValue = "1") Integer pageNum,
                                 @RequestParam(value = "pageSize", required = true, defaultValue = "10") Integer pageSize) {
        return brandService.findByPage(pageNum, pageSize);
    }

    @RequestMapping("/add")
    public ResultInfo add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return new ResultInfo(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "添加失败");
        }
    }

    @RequestMapping("/findById")
    public TbBrand findById(Long id) {
        return brandService.findById(id);
    }

    @RequestMapping("/update")
    public ResultInfo update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return new ResultInfo(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "修改失败");
        }
    }

    @RequestMapping("/deleteBrands")
    public ResultInfo deleteBrands(Long... ids) {
        try {
            brandService.deleteBrands(ids);
            return new ResultInfo(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "删除失败");
        }
    }

    @RequestMapping("/search")
    public PageResult findByNameOrFirstChar(@RequestBody TbBrand brand,
                                            @RequestParam(value = "pageNum", required = true, defaultValue = "1") Integer pageNum,
                                            @RequestParam(value = "pageSize", required = true, defaultValue = "10") Integer pageSize) {
        return brandService.findByPage(brand, pageNum, pageSize);
    }
}
