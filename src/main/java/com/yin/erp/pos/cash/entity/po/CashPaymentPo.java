package com.yin.erp.pos.cash.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 销售支付方式
 */
@Entity
@Table(name = "bill_cash_payment")
@Getter
@Setter
public class CashPaymentPo extends BasePo {

    /**
     * 销售ID
     */
    @Column(name = "cash_id")
    private String cashId;

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
     * 金额
     */
    @Column(name = "amount")
    private String amount;

    /**
     * 积分
     */
    @Column(name = "integral")
    private String integral;

}
