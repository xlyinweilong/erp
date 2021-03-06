package com.yin.erp.stock.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 库存
 *
 * @author yin
 */
@MappedSuperclass
@Getter
@Setter
public class BaseStockPo extends BasePo {

    @Column(name = "goods_id")
    private String goodsId;

    @Column(name = "goods_code")
    private String goodsCode;

    @Column(name = "goods_name")
    private String goodsName;

    @Column(name = "goods_color_id")
    private String goodsColorId;

    @Column(name = "goods_color_code")
    private String goodsColorCode;

    @Column(name = "goods_color_name")
    private String goodsColorName;

    @Column(name = "goods_size_id")
    private String goodsSizeId;

    @Column(name = "goods_size_name")
    private String goodsSizeName;

    @Column(name = "stock_count")
    private Integer stockCount = 0;

    @Column(name = "goods_group_id")
    private String goodsGroupId;

}
