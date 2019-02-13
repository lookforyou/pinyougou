package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.dao.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * 服务实现层
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;

    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbContent content) {
        contentMapper.insert(content);
        //清除缓存
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());
    }


    /**
     * 修改
     */
    @Override
    public void update(TbContent content) {
        //清除原来的缓存
        Long categoryId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
        redisTemplate.boundHashOps("content").delete(categoryId);
        contentMapper.updateByPrimaryKey(content);
        //清除新的缓存
        if (content.getCategoryId().longValue() != categoryId.longValue()) {
            redisTemplate.boundHashOps("content").delete(content.getCategoryId());
        }
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        TbContentExample example = new TbContentExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        //清除缓存
        List<TbContent> tbContents = contentMapper.selectByExample(example);
        Object[] categoryIds = new Object[tbContents.size()];
        for (int i = 0; i < categoryIds.length; i++) {
            categoryIds[i] = tbContents.get(i).getCategoryId();
        }
        redisTemplate.boundHashOps("content").delete(categoryIds);
        contentMapper.deleteByExample(example);
    }


    @Override
    public PageResult findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();
        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }
        }
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<TbContent> findByCategoryId(Long categoryId) {
        List<TbContent> content = (List<TbContent>) redisTemplate.boundHashOps("content").get(categoryId);
        if (content == null) {
            System.out.println("从数据库中读取数据");
            TbContentExample example = new TbContentExample();
            example.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
            example.setOrderByClause("sort_order");
            content = contentMapper.selectByExample(example);
            redisTemplate.boundHashOps("content").put(categoryId, content);
            return content;
        }
        System.out.println("从redis中读取数据");
        return content;
    }
}
