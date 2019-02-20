package com.yin.erp.activity.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 促销活动的促销规则范围
 *
 * @author yin
 */
@Entity
@Table(name = "activity_rule_range")
@Getter
@Setter
public class ActivityRuleRangePo extends BaseDataPo {

    /**
     * 活动id
     */
    @Column(name = "activity_Id")
    private String activityId;

    /**
     * 货品品牌
     */
    @Column(name = "goods_brand_id")
    private String goodsBrandId;

    /**
     * 货品品牌
     */
    @Column(name = "goods_brand_name")
    private String goodsBrandName;

    /**
     * 货品大类
     */
    @Column(name = "goods_category_id")
    private String goodsCategoryId;

    /**
     * 货品大类
     */
    @Column(name = "goods_category_name")
    private String goodsCategoryName;

    /**
     * 货品年份
     */
    @Column(name = "goods_year_id")
    private String goodsYearId;

    /**
     * 货品年份
     */
    @Column(name = "goods_year_name")
    private String goodsYearName;

    /**
     * 货品季节
     */
    @Column(name = "goods_season_id")
    private String goodsSeasonId;

    /**
     * 货品季节
     */
    @Column(name = "goods_season_name")
    private String goodsSeasonName;

    /**
     * 折扣
     */
    @Column(name = "discount")
    private BigDecimal discount;

}
