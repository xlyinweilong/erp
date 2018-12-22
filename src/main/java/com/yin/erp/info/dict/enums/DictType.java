package com.yin.erp.info.dict.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 字典类型
 *
 * @author yin
 */
public enum DictType {
    GOODS, CHANNEL;

    public String getMean() {
        switch (this) {
            case GOODS:
                return "货品";
            case CHANNEL:
                return "渠道";
            default:
                return null;
        }
    }

    public static List<Map> getMeanList() {
        List<Map> list = new ArrayList<>();
        for (DictType type : DictType.values()) {
            Map m = new HashMap(2);
            m.put("key", type.name());
            m.put("value", type.getMean());
            list.add(m);
        }
        return list;
    }

}
