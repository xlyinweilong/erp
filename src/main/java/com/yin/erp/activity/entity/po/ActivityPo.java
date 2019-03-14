package com.yin.erp.activity.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 促销活动
 *
 * @author yin
 */
@Entity
@Table(name = "activity")
@Getter
@Setter
public class ActivityPo extends BasePo {

    /**
     * 活动编号
     */
    @Column(name = "code")
    private String code;

    /**
     * 活动名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 促销方式
     */
    @Column(name = "type")
    private String type;


    /**
     * 活动描述
     */
    @Column(name = "marks")
    private String marks;

    /**
     * 活动开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "start_date")
    private Date startDate;

    /**
     * 活动结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "end_date")
    private Date endDate;

    /**
     * 执行星期（逗号分割）
     */
    @Column(name = "execute_week")
    private String executeWeek;

    /**
     * 活动状态
     */
    @Column(name = "status")
    private String status;

    /**
     * 优先级别
     */
    @Column(name = "priority")
    private Integer priority;

    @Column(name = "market_point_id")
    private String marketPointId;

    @Column(name = "market_point_code")
    private String marketPointCode;

    /**
     * 参加的渠道
     */
    @Column(name = "join_channel_type")
    private String joinChannelType;


    /**
     * 参加的会员
     */
    @Column(name = "join_vip_type")
    private String joinVipType;

    /**
     * 参加的会员
     */
    @Column(name = "vip_discount_type")
    private String vipDiscountType;

    @Column(name = "join_goods_type")
    private String joinGoodsType;

    @Column(name = "rule_type")
    private String ruleType;

    @JsonIgnore
    @OneToMany(mappedBy = "activityPo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ActivityChannelPo> activityChannelPoList;

    @JsonIgnore
    @OneToMany(mappedBy = "activityPo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ActivityConditionGoodsPo> activityConditionGoodsPoList;

    @JsonIgnore
    @OneToMany(mappedBy = "activityPo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ActivityGoodsPo> activityGoodsPoList;

    @JsonIgnore
    @OneToMany(mappedBy = "activityPo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ActivityRuleGoodsPo> activityRuleGoodsPoList;

    @JsonIgnore
    @OneToMany(mappedBy = "activityPo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ActivityRulePo> activityRulePoList;

    @JsonIgnore
    @OneToMany(mappedBy = "activityPo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ActivityRuleRangePo> activityRuleRangePoList;

    @JsonIgnore
    @OneToMany(mappedBy = "activityPo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ActivityVipPo> activityVipPoList;

}
