package com.yin.erp.vip.coupon.entity.vo;


import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 会员积分日志
 *
 * @author yin
 */
@Getter
@Setter
public class VipBalanceLogVo extends BasePageVo {


    /**
     * 会员Id
     */
    private String vipId;

    /**
     * 会员编号
     */
    private String vipCode;


    /**
     * 积分数量
     */
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 获取信息
     */
    private String message;

}
