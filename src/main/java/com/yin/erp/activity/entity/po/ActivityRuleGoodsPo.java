package com.yin.erp.activity.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.common.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 促销活动的促销规则商品
 *
 * @author yin
 */
@Entity
@Table(name = "activity_rule_goods")
@Getter
@Setter
public class ActivityRuleGoodsPo extends BaseDataPo {

    /**
     * 活动id
     */
    @Column(name = "activity_Id")
    private String activityId;

    /**
     * 货品ID
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 货品编号
     */
    @Column(name = "goods_code")
    private String goodsCode;

    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 价格
     */
    @Column(name = "price")
    private BigDecimal price;


    @JsonIgnore
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_Id", updatable = false, insertable = false)
    private ActivityPo activityPo;


}
