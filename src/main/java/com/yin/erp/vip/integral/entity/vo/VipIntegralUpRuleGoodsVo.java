package com.yin.erp.vip.integral.entity.vo;


import com.yin.common.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 会员积分获取规则
 *
 * @author yin
 */
@Getter
@Setter
public class VipIntegralUpRuleGoodsVo extends BasePageVo {

    private String id;

    private String searchKey;

    private String vipIntegralUpRuleId;

    /**
     * 对应的货品ID
     */
    private String goodsId;

    /**
     * 品牌
     */
    private String goodsBrandId;

    /**
     * 品类
     */
    private String goodsCategoryId;

    /**
     * 对应的年份
     */
    private String goodsYearId;

    /**
     * 对应的季节
     */
    private String goodsSeasonId;
}
