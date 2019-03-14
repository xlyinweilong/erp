package com.yin.erp.vip.integral.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 会员积分增加规则
 *
 * @author yin
 */
@Entity
@Table(name = "vip_integral_up_rule")
@Getter
@Setter
public class VipIntegralRulePo extends BasePo {

    /**
     * 生效时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "start_date")
    private Date startDate;

    /**
     * 失效时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
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

    @JsonIgnore
    @OneToMany(mappedBy = "vipIntegralRulePo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<VipIntegralRuleGoodsPo> vipIntegralRuleGoodsPoList;

}
