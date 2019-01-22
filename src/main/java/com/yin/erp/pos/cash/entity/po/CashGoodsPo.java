package com.yin.erp.pos.cash.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售货品
 */
@Entity
@Table(name = "bill_cash_goods")
@Getter
@Setter
public class CashGoodsPo extends BasePo {

    /**
     * 销售ID
     */
    @Column(name = "cash_id")
    private String cashId;

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
     * 颜色ID
     */
    @Column(name = "color_id")
    private String colorId;

    /**
     * 颜色编号
     */
    @Column(name = "color_code")
    private String colorCode;

    /**
     * 颜色名称
     */
    @Column(name = "color_name")
    private String colorName;

    /**
     * 尺码ID
     */
    @Column(name = "size_id")
    private String sizeId;

    /**
     * 尺码名称
     */
    @Column(name = "size_name")
    private String sizeName;

    /**
     * 单据时间
     */
    @Column(name = "bill_date")
    private LocalDate billDate;

    /**
     * 渠道ID
     */
    @Column(name = "channel_id")
    private String channelId;

    /**
     * 渠道编号
     */
    @Column(name = "channel_code")
    private String channelCode;

    /**
     * 渠道名称
     */
    @Column(name = "channel_name")
    private String channelName;

    /**
     * 金额
     */
    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * 单价
     */
    @Column(name = "price")
    private BigDecimal price = BigDecimal.ZERO;

    /**
     * 吊牌价
     */
    @Column(name = "tag_price")
    private BigDecimal tagPrice = BigDecimal.ZERO;

    /**
     * 单据数量
     */
    @Column(name = "bill_count")
    private Integer billCount = 0;

    /**
     * 营业员ID
     */
    @Column(name = "employ_id")
    private String employId;

    /**
     * 营业员编号
     */
    @Column(name = "employ_code")
    private String employCode;

    /**
     * 营业员名称
     */
    @Column(name = "employ_name")
    private String employName;

}
