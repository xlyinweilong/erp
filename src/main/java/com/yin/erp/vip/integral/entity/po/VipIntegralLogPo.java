package com.yin.erp.vip.integral.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 会员信息
 *
 * @author yin
 */
@Entity
@Table(name = "vip_integral_log")
@Getter
@Setter
public class VipIntegralLogPo extends BasePo {

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
    @Column(name = "integral")
    private Integer integral = 0;

    /**
     * 获取信息
     */
    @Column(name = "message")
    private String message;


}
