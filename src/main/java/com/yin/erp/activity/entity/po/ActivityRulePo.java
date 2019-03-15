package com.yin.erp.activity.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.common.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 促销活动的规则
 *
 * @author yin
 */
@Entity
@Table(name = "activity_rule")
@Getter
@Setter
public class ActivityRulePo extends BaseDataPo {

    /**
     * 活动id
     */
    @Column(name = "activity_Id")
    private String activityId;

    /**
     * 条件数量
     */
    @Column(name = "quantity")
    private Integer quantity;

    /**
     * 条件金额
     */
    @Column(name = "amount")
    private BigDecimal amount;

    /**
     * 折扣
     */
    @Column(name = "discount")
    private BigDecimal discount;

    /**
     * 立减金额
     */
    @Column(name = "plus_Amount")
    private BigDecimal plusAmount;

    /**
     * 赠送数量
     */
    @Column(name = "give_count")
    private Integer giveCount;

    @JsonIgnore
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_Id", updatable = false, insertable = false)
    private ActivityPo activityPo;
}
