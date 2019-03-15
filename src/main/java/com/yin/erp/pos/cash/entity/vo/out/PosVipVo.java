package com.yin.erp.pos.cash.entity.vo.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.common.entity.vo.BaseVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * POS会员信息
 *
 * @author yin.weilong
 * @date 2019.02.12
 */
@Getter
@Setter
public class PosVipVo extends BaseVo {

    private String id;

    /**
     * 会员编号
     */
    private String code;

    /**
     * 会员名称
     */
    private String name;

    /**
     * 会员性别
     */
    private Integer sex = -1;

    /**
     * 余额
     */
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 积分
     */
    private Integer integral = 0;

    /**
     * 经验值
     */
    private Integer xpValue = 0;

    /**
     * 开发日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate openDate;

    /**
     * 折扣
     */
    private BigDecimal discount;


    /**
     * 等级ID
     */
    private String gradeId;

    /**
     * 多少积分为1元
     */
    private Integer integralToMoney;
}
