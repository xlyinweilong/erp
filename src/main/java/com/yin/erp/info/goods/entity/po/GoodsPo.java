package com.yin.erp.info.goods.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 货品资料
 *
 * @author yin
 */
@Entity
@Table(name = "i_goods", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class GoodsPo extends BasePo {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "size_group_id")
    private String sizeGroupId;

    @Column(name = "size_group_name")
    private String sizeGroupName;

    @Column(name = "brand_id")
    private String brandId;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "category2_id")
    private String category2Id;

    @Column(name = "category2_name")
    private String category2Name;

    @Column(name = "series_id")
    private String seriesId;

    @Column(name = "series_name")
    private String seriesName;

    @Column(name = "pattern_id")
    private String patternId;

    @Column(name = "pattern_name")
    private String patternName;

    @Column(name = "style_id")
    private String styleId;

    @Column(name = "style_name")
    private String styleName;

    @Column(name = "season_id")
    private String seasonId;

    @Column(name = "season_name")
    private String seasonName;

    @Column(name = "year_id")
    private String yearId;

    @Column(name = "year_name")
    private String yearName;

    @Column(name = "sex_id")
    private String sexId;

    @Column(name = "sex_name")
    private String sexName;

    @Column(name = "goods_group_id")
    private String goodsGroupId;

    @Column(name = "goods_group_name")
    private String goodsGroupName;

    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "supplier_code")
    private String supplierCode;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "tag_price_1")
    private BigDecimal tagPrice1;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodsPo goodsPo = (GoodsPo) o;
        return Objects.equals(code, goodsPo.code);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code);
    }
}
