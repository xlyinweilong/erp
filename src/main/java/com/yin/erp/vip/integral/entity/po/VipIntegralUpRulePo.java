package com.yin.erp.vip.integral.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * 会员积分增加规则
 *
 * @author yin
 */
@Entity
@Table(name = "vip_integral_up_rule")
@Getter
@Setter
public class VipIntegralUpRulePo extends BasePo {

    /**
     * 生效时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "start_date")
    private Date startDate;

    /**
     * 失效时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Column(name = "end_date")
    private Date endDate;

    /**
     * 规则名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 会员等级Id
     */
    @Column(name = "vip_grade_id")
    private String vipGradeId;

    /**
     * 增加的积分数量
     */
    @Column(name = "integral")
    private Integer integral = 0;

    /**
     * 优先级别
     */
    @Column(name = "priority")
    private Integer priority = 0;

}
