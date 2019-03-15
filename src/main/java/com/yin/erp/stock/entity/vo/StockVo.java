package com.yin.erp.stock.entity.vo;

import com.yin.common.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

/**
 * kuVO
 *
 * @author yin
 */
@Getter
@Setter
public class StockVo extends BasePageVo {

    private String channelId;

    private String channelCode;

    private String channelName;

    private String warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private String goodsId;

    private String goodsCode;

    private String goodsName;

    private String goodsColorId;

    private String goodsColorCode;

    private String goodsColorName;

    private String goodsSizeId;

    private String goodsSizeName;

    private Integer stockCount = 0;

}
