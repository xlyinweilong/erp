package com.yin.erp.vip.coupon.entity.vo;


import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 会员余额充值
 *
 * @author yin
 */
@Getter
@Setter
public class VipBalanceAddVo extends BasePageVo {

    private String id;

    private String searchKey;

    /**
     * 会员ID
     */
    private String vipId;

    /**
     * 会员编号
     */
    private String vipCode;

    /**
     * 会员名称
     */
    private String vipName;

    /**
     * 余额
     */
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 积分
     */
    private Integer integral = 0;

    /**
     * 经验
     */
    private Integer xp = 0;

    /**
     * 失效
     */
    private boolean invalid = false;

}
