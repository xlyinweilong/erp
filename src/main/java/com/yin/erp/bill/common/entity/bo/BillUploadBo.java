package com.yin.erp.bill.common.entity.bo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 单据上传BO
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
@Getter
@Setter
@Builder
public class BillUploadBo {

    private Date billDate;

    private String supplierCode;

    private String warehouseCode;

    private String goodsCode;

    private String goodsColorCode;

    private String goodsColorName;

    private String goodsSizeName;

    private BigDecimal price;

    private Integer billCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillUploadBo that = (BillUploadBo) o;
        return Objects.equals(goodsCode, that.goodsCode) &&
                Objects.equals(goodsColorCode, that.goodsColorCode) &&
                Objects.equals(goodsColorName, that.goodsColorName) &&
                Objects.equals(goodsSizeName, that.goodsSizeName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(goodsCode, goodsColorCode, goodsColorName, goodsSizeName);
    }
}
