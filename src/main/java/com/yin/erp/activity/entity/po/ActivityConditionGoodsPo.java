package com.yin.erp.activity.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.common.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 促销活动的条件商品
 *
 * @author yin
 */
@Entity
@Table(name = "activity_condition_goods")
@Getter
@Setter
public class ActivityConditionGoodsPo extends BaseDataPo {

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

    @JsonIgnore
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_Id", updatable = false, insertable = false)
    private ActivityPo activityPo;

}
