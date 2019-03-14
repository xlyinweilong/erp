package com.yin.erp.bill.common.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 单据详情实体
 *
 * @author yin.weilong
 * @date 2018.12.01
 */
@MappedSuperclass
@Getter
@Setter
public class BillDetailPo extends BaseDataPo {

    /**
     * 单据时间
     */
    @Column(name = "bill_date")
    private LocalDate billDate;

    /**
     * 单据ID
     */
    @Column(name = "bill_id")
    private String billId;

    /**
     * 单据货品ID
     */
    @Column(name = "bill_goods_id")
    private String billGoodsId;

    /**
     * 货品ID
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 货品编号
     */
    @Column(name = "goods_code")
    private String goodsCode;

    /**
     * 货品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 货品颜色ID
     */
    @Column(name = "goods_color_id")
    private String goodsColorId;

    /**
     * 货品颜色编号
     */
    @Column(name = "goods_color_code")
    private String goodsColorCode;

    /**
     * 货品颜色名称
     */
    @Column(name = "goods_color_name")
    private String goodsColorName;

    /**
     * 货品尺码ID
     */
    @Column(name = "goods_size_id")
    private String goodsSizeId;


    /**
     * 货品尺码名称
     */
    @Column(name = "goods_size_name")
    private String goodsSizeName;

    /**
     * 价格
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * 吊牌价
     */
    @Column(name = "tag_price")
    private BigDecimal tagPrice;

    /**
     * 数量
     */
    @Column(name = "bill_count")
    private Integer billCount;

    @Transient
    private Integer billOrder = 0;
}
