package com.yin.erp.info.dict.entity.vo;

import com.yin.erp.base.entity.vo.in.BasePageVo;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 字段VO
 *
 * @author yin
 */
@Getter
@Setter
public class DictVo extends BasePageVo {

    private String id;

    private String code;

    private String name;

    private String type1;

    private String type2;

    private Integer orderIndex;

    private List<DictSizePo> sizeList;

    /**
     * 搜索的关键字，后期需要使用搜索引擎
     */
    private String searchKey;

}
