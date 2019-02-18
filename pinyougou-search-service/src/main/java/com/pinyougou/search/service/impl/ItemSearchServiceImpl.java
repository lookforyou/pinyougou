package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询主方法
     *
     * @param searchMap 用户输入的参数
     * @return 返回结果集
     */
    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        //解决用户搜索空格
        String keywords = (String) searchMap.get("keywords");
        if (keywords.contains(" ")) {
            map.put("keywords", keywords.replace(" ", ""));
        }
        map.putAll(searchHighlight(searchMap));
        map.put("categories", searchCategory(searchMap));
        if ("".equals((String) searchMap.get("category"))) {
            if (searchCategory(searchMap).size() > 0) {
                map.putAll(searchBrandsAndSpecs(searchCategory(searchMap).get(0)));
            }
        } else {
            map.putAll(searchBrandsAndSpecs((String) searchMap.get("category")));
        }
        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByIds(Long[] ids) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid").in(Arrays.asList(ids));
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 获取高亮结果
     *
     * @param searchMap 用户搜索的关键字
     * @return 返回高亮结果集
     */
    private Map<String, Object> searchHighlight(Map<String, Object> searchMap) {
        Map<String, Object> map = new HashMap<String, Object>();
        HighlightQuery query = new SimpleHighlightQuery();
        //构建高亮选项对象并指定要高亮显示的域
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title");
        highlightOptions.setSimplePrefix("<em style='color:red'>");//前缀
        highlightOptions.setSimplePostfix("</em>");//后缀
        query.setHighlightOptions(highlightOptions);
        //构建查询条件
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //分类过滤
        if (!"".equals(searchMap.get("category"))) {
            Criteria criteria1 = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery();
            filterQuery.addCriteria(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //品牌过滤
        if (!"".equals(searchMap.get("brand"))) {
            Criteria criteria1 = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery();
            filterQuery.addCriteria(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> spec = (Map<String, String>) searchMap.get("spec");
            for (String key : spec.keySet()) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_spec_" + key).is(spec.get(key));
                filterQuery.addCriteria(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        //价格过滤
        if (!"".equals(searchMap.get("price"))) {
            String price = (String) searchMap.get("price");
            String[] split = price.split("-");
            if (!"0".equals(split[0])) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(split[0]);
                filterQuery.addCriteria(criteria1);
                query.addFilterQuery(filterQuery);
            }
            if (!"*".equals(split[1])) {
                FilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(split[1]);
                filterQuery.addCriteria(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        //分页
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            pageNo = 1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            pageSize = 40;
        }
        query.setOffset((pageNo - 1) * pageSize);//起始索引
        query.setRows(pageSize);//显示多少条
        //排序
        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if (!"".equals(sortField) && !"".equals(sortValue)) {
            if ("ASC".equals(sortValue)) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if ("DESC".equals(sortValue)) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }
        //返回高亮入口对象
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //获得高亮入口结果集
        List<HighlightEntry<TbItem>> highlightEntryList = highlightPage.getHighlighted();
        //遍历高亮入口结果集
        for (HighlightEntry<TbItem> entry : highlightEntryList) {
            if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size() > 0) {
                //获得每个实体对象
                TbItem tbItem = entry.getEntity();
                //把高亮数据设置到实体对象
                tbItem.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("rows", highlightPage.getContent());
        map.put("totalPages", highlightPage.getTotalPages());
        map.put("total", highlightPage.getTotalElements());
        return map;
    }

    /**
     * 查询分组
     *
     * @param searchMap 用户搜索的关键字
     * @return 返回分组结果集
     */
    private List<String> searchCategory(Map<String, Object> searchMap) {
        List<String> list = new ArrayList<String>();
        Query query = new SimpleQuery();
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        GroupResult<TbItem> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<TbItem> groupEntry : groupEntries) {
            list.add(groupEntry.getGroupValue());
        }
        return list;
    }

    /**
     * 根据商品分类名称查询品牌和规格列表
     *
     * @param category 商品分类名称
     * @return 返回结果集
     */
    private Map<String, List<Map>> searchBrandsAndSpecs(String category) {
        Map<String, List<Map>> map = new HashMap<String, List<Map>>();
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        if (typeId != null) {
            List<Map> brands = (List<Map>) redisTemplate.boundHashOps("brands").get(typeId);
            map.put("brands", brands);
            List<Map> specs = (List<Map>) redisTemplate.boundHashOps("specs").get(typeId);
            map.put("specs", specs);
        }
        return map;
    }
}
