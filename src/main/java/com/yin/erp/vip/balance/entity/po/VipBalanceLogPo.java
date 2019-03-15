package com.yin.erp.vip.balance.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 会员余额日志
 *
 * @author yin
 */
@Entity
@Table(name = "vip_balance_log")
@Getter
@Setter
public class VipBalanceLogPo extends BasePo {

    /**
     * 会员Id
     */
    @Column(name = "vip_id")
    private String vipId;

    /**
     * 会员编号
     */
    @Column(name = "vip_code")
    private String vipCode;


    /**
     * 积分数量
     */
    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 获取信息
     */
    @Column(name = "message")
    private String message;


}
