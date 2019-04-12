package com.yin.erp.vip.integral.entity.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.erp.vip.common.vo.BaseVipSearchVo;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 会员积分获取规则
 *
 * @author yin
 */
@Getter
@Setter
public class VipIntegralUpRuleVo extends BaseVipSearchVo {

    private String id;

    private String code;

    /**
     * 用户ID
     */
    private String vipId;

    /**
     * 是否使用过
     */
    private Boolean used;

    /**
     * 生效时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;

    /**
     * 失效时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;

    /**
     * 规则名称
     */
    private String name;

    /**
     * 会员等级Id
     */
    private String vipGradeId;

    /**
     * 增加的积分数量
     */
    private Integer integral = 0;

    /**
     * 优先级别
     */
    private Integer priority = 0;

}
