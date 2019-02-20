package com.yin.erp.pos.cash.entity.vo.in;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * POS商品
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class PosCashGoods {

    private String id;

    private String code;

    private String name;

    private String goodsColorId;

    private String goodsColorCode;

    private String goodsColorName;

    private String goodsSizeId;

    private String goodsSizeName;

    private BigDecimal amount;

    private BigDecimal price;

    private BigDecimal tagPrice;

    private Integer billCount;

    private String employId;

    private String employName;

    private String employCode;

    private String remarks;

    private BigDecimal vipDiscount;

    private String activityId;

    private BigDecimal diyPrice;

    private String goodsBrandId;

    private String goodsCategoryId;

    private String goodsSeasonId;

    private String goodsYearId;

}
