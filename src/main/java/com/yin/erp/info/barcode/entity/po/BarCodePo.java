package com.yin.erp.info.barcode.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 条形码资料
 *
 * @author yin
 */
@Entity
@Table(name = "i_bar_code", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class BarCodePo extends BasePo {

    @Column(name = "code")
    private String code;

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

    @Column(name = "goods_in_size_id")
    private String goodsInSizeId;

    @Column(name = "goods_in_size_name")
    private String goodsInSizeName;

    @Column(name = "goods_size_id")
    private String goodsSizeId;

    @Column(name = "goods_size_name")
    private String goodsSizeName;


}
