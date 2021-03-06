package com.yin.erp.vip.grade.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 会员等级
 *
 * @author yin
 */
@Entity
@Table(name = "vip_grade")
@Getter
@Setter
public class VipGradePo extends BasePo {

    /**
     * 等级名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 深度
     */
    @Column(name = "index_depth")
    private Integer indexDepth;

    /**
     * 默认等级
     */
    @Column(name = "default_grade")
    private boolean defaultGrade;

    /**
     * 最少经验值
     */
    @Column(name = "lowest_xp_value")
    private Integer lowestXpValue;

    /**
     * 折扣
     */
    @Column(name = "discount")
    private BigDecimal discount;



}
