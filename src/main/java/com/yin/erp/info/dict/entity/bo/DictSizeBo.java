package com.yin.erp.info.dict.entity.bo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 尺码bo
 *
 * @author yin.weilong
 * @date 2018.11.13
 */
@Getter
@Setter
@Builder
public class DictSizeBo {
    private String id;

    private String sizeGroupId;

    private String name;
}
