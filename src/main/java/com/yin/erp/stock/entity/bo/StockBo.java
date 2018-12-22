package com.yin.erp.stock.entity.bo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 库存BO
 *
 * @author yin.weilong
 * @date 2018.12.21
 */
@Getter
@Setter
public class StockBo {

    private String channelId;

    private String warehouseId;

    @NotBlank
    private String goodsId;

    @NotBlank
    private String goodsColorId;

    @NotBlank
    private String goodsInSizeId;

    @NotBlank
    private String goodsSizeId;

    @NotNull
    private Integer stockCount;

    public StockBo() {
    }


    public StockBo(String channelId, String warehouseId, @NotBlank String goodsId, @NotBlank String goodsColorId, @NotBlank String goodsInSizeId, @NotBlank String goodsSizeId, @NotNull Integer stockCount) {
        this.channelId = channelId;
        this.warehouseId = warehouseId;
        this.goodsId = goodsId;
        this.goodsColorId = goodsColorId;
        this.goodsInSizeId = goodsInSizeId;
        this.goodsSizeId = goodsSizeId;
        this.stockCount = stockCount;
    }
}
