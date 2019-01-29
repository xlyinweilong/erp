package com.yin.erp.vip.xp.entity.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.erp.base.entity.vo.in.BasePageVo;
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
public class VipXpUpRuleVo extends BasePageVo {

    private String id;

    private String searchKey;

    /**
     * 生效时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;

    /**
     * 失效时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
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
    private Integer xp = 0;

    /**
     * 优先级别
     */
    private Integer priority = 0;

}
