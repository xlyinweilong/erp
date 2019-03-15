package com.yin.erp.vip.integral.entity.po;

import com.yin.common.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 会员积分兑换金额
 *
 * @author yin
 */
@Entity
@Table(name = "vip_integral_to_amount")
@Getter
@Setter
public class VipIntegralToAmountPo extends BaseDataPo {


    @Column(name = "grade_id")
    private String gradeId;

    /**
     * 积分数量
     */
    @Column(name = "integral")
    private Integer integral = 0;


}
