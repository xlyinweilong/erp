package com.yin.erp.info.dict.enums;

import java.util.*;

/**
 * 渠道字典类型
 *
 * @author yin
 */
public enum DictSupplierType {
    SUPPLIER_GROUP;

    public String getMean() {
        switch (this) {
            case SUPPLIER_GROUP:
                return "组";
            default:
                return null;
        }
    }

    /**
     * 判断是否含有编号
     *
     * @return true有编号 false没有编号
     */
    public boolean isHaveCode() {
        DictSupplierType[] d = new DictSupplierType[]{};
        return Arrays.asList(d).contains(this);
    }

    public static List<Map> getMeanList() {
        List<Map> list = new ArrayList<>();
        for (DictSupplierType type : DictSupplierType.values()) {
            Map m = new HashMap(3);
            m.put("key", type.name());
            m.put("value", type.getMean());
            m.put("type", type.isHaveCode());
            list.add(m);
        }
        return list;
    }
}
