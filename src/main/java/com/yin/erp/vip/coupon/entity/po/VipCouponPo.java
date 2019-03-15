package com.yin.erp.vip.coupon.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠卷
 *
 * @author yin
 */
@Entity
@Table(name = "vip_coupon")
@Getter
@Setter
public class VipCouponPo extends BasePo {


    /**
     * 编号
     */
    @Column(name = "code")
    private String code;

    /**
     * 所属会员
     */
    @Column(name = "vip_id")
    private String vipId;

    @Column(name = "vip_code")
    private String vipCode;

    @Column(name = "vip_name")
    private String vipName;

    /**
     * 抵金额、折扣
     */
    @Column(name = "type")
    private String type;

    /**
     * 金额
     */
    @Column(name = "amount")
    private BigDecimal amount = BigDecimal.ZERO;

    /**
     * 条件金额
     */
    @Column(name = "condition_amount")
    private BigDecimal conditionAmount = BigDecimal.ZERO;

    /**
     * 折扣
     */
    @Column(name = "discount")
    private BigDecimal discount = BigDecimal.ZERO;

    /**
     * 开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "start_date")
    private Date startDate;

    /**
     * 开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "end_date")
    private Date endDate;

    /**
     * 已经使用
     */
    @Column(name = "used")
    private boolean used = false;


}
