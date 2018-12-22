package com.yin.erp.info.dict.entity.vo.out;

import com.yin.erp.base.entity.vo.BaseVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 尺码组VO
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Getter
@Setter
public class DictSizeGroupVo extends BaseVo {

    private String id;

    private String name;

    private String type1;

    private String type2;

    private List<String> list;
}
