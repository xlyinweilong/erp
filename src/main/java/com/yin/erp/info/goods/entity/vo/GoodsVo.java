package com.yin.erp.info.goods.entity.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yin.erp.base.entity.vo.in.BasePageVo;
import com.yin.erp.info.dict.entity.bo.DictSizeBo;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 货品资料VO
 *
 * @author yin
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsVo extends BasePageVo {

    /**
     * 关键词
     */
    private String searchKey;

    private String id;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String sizeGroupId;

    private String sizeGroupName;

    private String brandId;

    private String brandName;

    private String categoryId;

    private String categoryName;

    private String category2Id;

    private String category2Name;

    private String seriesId;

    private String seriesName;

    private String patternId;

    private String patternName;

    private String styleId;

    private String styleName;

    private String seasonId;

    private String seasonName;

    private String yearId;

    private String yearName;

    private String sexId;

    private String sexName;

    private String supplierId;

    private String supplierCode;

    private String supplierName;

    private String goodsGroupId;

    private String goodsGroupName;

    private BigDecimal tagPrice1;

    @NotNull
    private List<String> colorIdList;

    private List<GoodsColorPo> colorList;

    private List<DictSizeBo> sizeList;

    private String channelId;

}
