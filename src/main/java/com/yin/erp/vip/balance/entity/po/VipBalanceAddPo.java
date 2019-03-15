package com.yin.erp.vip.balance.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 会员充值
 *
 * @author yin
 */
@Entity
@Table(name = "vip_balance_add")
@Getter
@Setter
public class VipBalanceAddPo extends BasePo {


    /**
     * 会员ID
     */
    @Column(name = "vip_id")
    private String vipId;

    /**
     * 会员编号
     */
    @Column(name = "vip_code")
    private String vipCode;

    /**
     * 会员名称
     */
    @Column(name = "vip_name")
    private String vipName;

    /**
     * 余额
     */
    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 积分
     */
    @Column(name = "integral")
    private Integer integral = 0;

    /**
     * 经验
     */
    @Column(name = "xp")
    private Integer xp = 0;

    /**
     * 失效
     */
    @Column(name = "invalid")
    private boolean invalid = false;

    @Column(name = "invalid_user_id")
    private String invalidUserId;

    @Column(name = "invalid_user_name")
    private String invalidUserName;

    @Column(name = "create_user_id")
    private String createUserId;

    @Column(name = "create_user_name")
    private String createUserName;

}
