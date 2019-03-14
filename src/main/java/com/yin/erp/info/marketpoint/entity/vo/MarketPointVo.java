package com.yin.erp.info.marketpoint.entity.vo;

import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 条形码VO
 *
 * @author yin
 */
@Getter
@Setter
public class MarketPointVo extends BasePageVo {

    private String searchKey;

    /**
     * ID
     */
    private String id;

    /**
     * 编号
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 扣点
     */
    private BigDecimal point;

    /**
     * 备注
     */
    private String remarks;

}
