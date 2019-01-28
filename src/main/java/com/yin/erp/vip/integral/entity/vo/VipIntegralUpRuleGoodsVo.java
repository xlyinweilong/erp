package com.yin.erp.vip.integral.entity.vo;


import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

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
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 品牌
     */
    @Column(name = "goods_brand_id")
    private String goodsBrandId;

    /**
     * 品类
     */
    @Column(name = "goods_category_id")
    private String goodsCategoryId;

    /**
     * 对应的年份
     */
    @Column(name = "goods_year_id")
    private String goodsYearId;

    /**
     * 对应的季节
     */
    @Column(name = "goods_season_id")
    private String goodsSeasonId;
}
