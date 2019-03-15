package com.yin.erp.pos.cash.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售支付方式
 */
@Entity
@Table(name = "bill_pos_cash_payment")
@Getter
@Setter
public class PosCashPaymentPo extends BasePo {

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
     * 支付方式
     */
    @Column(name = "payment_id")
    private String paymentId;

    /**
     * 支付方式名称
     */
    @Column(name = "payment_name")
    private String paymentName;

    /**
     * 支付方式sys
     */
    @Column(name = "sys")
    private String sys;

    /**
     * 支付方式sysType
     */
    @Column(name = "sys_type")
    private String sysType;

    /**
     * 金额
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * 积分
     */
    @Column(name = "integral")
    private Integer integral;

}
