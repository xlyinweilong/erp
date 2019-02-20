package com.yin.erp.pos.cash.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售货品
 */
@Entity
@Table(name = "bill_pos_cash_detail")
@Getter
@Setter
public class PosCashDetailPo extends BasePo {

    /**
     * 销售ID
     */
    @Column(name = "bill_id")
    private String billId;

    /**
     * 单据时间
     */
    @Column(name = "bill_date")
    private Date billDate;

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
     * 颜色ID
     */
    @Column(name = "goods_color_id")
    private String goodsColorId;

    /**
     * 颜色编号
     */
    @Column(name = "goods_color_code")
    private String goodsColorCode;

    /**
     * 颜色名称
     */
    @Column(name = "goods_color_name")
    private String goodsColorName;

    /**
     * 尺码ID
     */
    @Column(name = "goods_size_id")
    private String goodsSizeId;

    /**
     * 尺码名称
     */
    @Column(name = "goods_size_name")
    private String goodsSizeName;

    /**
     * 渠道ID
     */
    @Column(name = "channel_id")
    private String channelId;

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

    /**
     * 获得的积分
     */
    @Column(name = "integral")
    private Integer integral;

    /**
     * 获得的经验
     */
    @Column(name = "xp")
    private Integer xp;

    /**
     * 状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;


    @Column(name = "vip_discount")
    private BigDecimal vipDiscount;


    @Column(name = "activity_id")
    private String activityId;


    @Column(name = "diy_price")
    private BigDecimal diyPrice;

}
