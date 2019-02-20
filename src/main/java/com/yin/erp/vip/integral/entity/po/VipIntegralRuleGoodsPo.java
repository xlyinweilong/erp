package com.yin.erp.vip.integral.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 会员积分增加规则-货品
 *
 * @author yin
 */
@Entity
@Table(name = "vip_integral_up_rule_goods")
@Getter
@Setter
public class VipIntegralRuleGoodsPo extends BasePo {

    /**
     * 规则ID
     */
    @Column(name = "vip_integral_up_rule_id")
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

    @Column(name = "goods_brand_name")
    private String goodsBrandName;
    /**
     * 品类
     */
    @Column(name = "goods_category_id")
    private String goodsCategoryId;

    @Column(name = "goods_category_name")
    private String goodsCategoryName;

    /**
     * 对应的年份
     */
    @Column(name = "goods_year_id")
    private String goodsYearId;

    @Column(name = "goods_year_name")
    private String goodsYearName;

    /**
     * 对应的季节
     */
    @Column(name = "goods_season_id")
    private String goodsSeasonId;

    @Column(name = "goods_season_name")
    private String goodsSeasonName;

}
