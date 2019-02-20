package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.dao.TbGoodsDescMapper;
import com.pinyougou.dao.TbGoodsMapper;
import com.pinyougou.dao.TbItemCatMapper;
import com.pinyougou.dao.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${pageDir}")
    private String pageDir;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Autowired
    private TbItemMapper tbItemMapper;

    @Override
    public boolean genItemHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Writer out = null;
        try {
            Template template = configuration.getTemplate("item.ftl");
            Map<String, Object> dataModel = new HashMap<String, Object>();
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods", tbGoods);
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc", tbGoodsDesc);
            String category1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String category2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String category3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();
            dataModel.put("category1", category1);
            dataModel.put("category2", category2);
            dataModel.put("category3", category3);
            TbItemExample example = new TbItemExample();
            example.createCriteria().andGoodsIdEqualTo(tbGoods.getId()).andStatusEqualTo("1");
            example.setOrderByClause("is_default DESC");
            List<TbItem> tbItems = tbItemMapper.selectByExample(example);
            dataModel.put("items", tbItems);
            out = new FileWriterWithEncoding(pageDir + goodsId + ".html", "UTF-8");
            template.process(dataModel, out);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
