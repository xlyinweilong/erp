package com.yin.erp.info.barcode.entity.vo;

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
public class BarCodeVo extends BasePageVo {

    private String id;

    private String code;

    private String goodsId;

    private String goodsCode;

    private String goodsName;

    private String goodsColorId;

    private String goodsColorCode;

    private String goodsColorName;

    private String goodsInSizeId;

    private String goodsInSizeName;

    private String goodsSizeId;

    private String goodsSizeName;

    private BigDecimal tagPrice;

    private BigDecimal price;

}
