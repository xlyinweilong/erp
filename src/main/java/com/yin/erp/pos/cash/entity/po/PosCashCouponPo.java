package com.yin.erp.pos.cash.entity.po;

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
 * 销售使用的待用卷
 */
@Entity
@Table(name = "bill_pos_cash_coupon")
@Getter
@Setter
public class PosCashCouponPo extends BasePo {

    /**
     * 销售ID
     */
    @Column(name = "bill_id")
    private String billId;

    /**
     * 单据时间
     */
    @Column(name = "bill_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date billDate;

    /**
     * 待用卷
     */
    @Column(name = "coupon_id")
    private String couponId;

    /**
     * 金额
     */
    @Column(name = "coupon_amount")
    private BigDecimal couponAmount;

}
