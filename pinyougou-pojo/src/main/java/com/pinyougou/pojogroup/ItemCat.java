package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbItemCat;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ItemCat implements Serializable {
    private TbItemCat tbItemCat;
    private List<Map> tbTypeTemplate;

    public TbItemCat getTbItemCat() {
        return tbItemCat;
    }

    public void setTbItemCat(TbItemCat tbItemCat) {
        this.tbItemCat = tbItemCat;
    }

    public List<Map> getTbTypeTemplate() {
        return tbTypeTemplate;
    }

    public void setTbTypeTemplate(List<Map> tbTypeTemplate) {
        this.tbTypeTemplate = tbTypeTemplate;
    }
}
