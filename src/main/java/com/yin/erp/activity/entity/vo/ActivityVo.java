package com.yin.erp.activity.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.common.entity.vo.in.BasePageVo;
import com.yin.erp.activity.entity.po.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 促销活动
 */
@Getter
@Setter
public class ActivityVo extends BasePageVo {

    private String searchKey;

    private String id;

    private String code;

    private String name;

    private String type;

    private Integer priority;

    private String marketPointId;

    private String marketPointCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    /**
     * 执行星期
     */
    private String[] executeWeek;

    private String status;

    private String marks;

    private String joinChannelType;

    private String joinVipType;

    private String vipDiscountType;

    private String joinGoodsType;

    private String ruleType;

    private List<ActivityChannelPo> activityChannelList;

    private List<ActivityVipPo> activityVipList;

    private List<ActivityGoodsPo> activityGoodsList;

    private List<ActivityConditionGoodsPo> activityConditionGoodsList;

    private List<ActivityRulePo> activityRuleList;

    private List<ActivityRuleGoodsPo> activityRuleGoodsList;

    private List<ActivityRuleRangePo> activityRuleRangeList;
}
