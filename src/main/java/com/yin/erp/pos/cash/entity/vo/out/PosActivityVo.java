package com.yin.erp.pos.cash.entity.vo.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.erp.activity.entity.po.*;
import com.yin.erp.base.entity.vo.BaseVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * POS活动信息
 *
 * @author yin.weilong
 * @date 2019.02.12
 */
@Getter
@Setter
public class PosActivityVo extends BaseVo {

    private String id;

    /**
     * 活动编号
     */
    private String code;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 促销方式
     */
    private String type;

    /**
     * 扣点
     */
    private BigDecimal points = BigDecimal.ZERO;

    /**
     * 活动描述
     */
    private String marks;

    /**
     * 活动开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    /**
     * 活动结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    /**
     * 执行星期（逗号分割）
     */
    private String executeWeek;

    /**
     * 活动状态
     */
    private String status;

    /**
     * 优先级别
     */
    private Integer priority;

    /**
     * 参加的渠道
     */
    private String joinChannelType;


    /**
     * 参加的会员
     */
    private String joinVipType;

    /**
     * 参加的会员
     */
    private String vipDiscountType;

    private String joinGoodsType;

    private String ruleType;

    private List<ActivityVipPo> activityVipList;

    private List<ActivityGoodsPo> activityGoodsList;

    private List<ActivityConditionGoodsPo> activityConditionGoodsList;

    private List<ActivityRulePo> activityRuleList;

    private List<ActivityRuleGoodsPo> activityRuleGoodsList;

    private List<ActivityRuleRangePo> activityRuleRangeList;
}
