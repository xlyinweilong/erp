package com.yin.erp.vip.balance.entity.vo;


import com.yin.common.entity.vo.in.BasePageVo;
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
