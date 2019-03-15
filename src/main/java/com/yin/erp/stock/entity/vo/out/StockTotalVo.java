package com.yin.erp.stock.entity.vo.out;

import com.yin.common.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * kuVO
 *
 * @author yin
 */
@Getter
@Setter
public class StockTotalVo extends BasePageVo {

    private Integer stockCount = 0;

    private BigDecimal stockAmount;

    public StockTotalVo() {
    }

    public StockTotalVo(Integer stockCount, BigDecimal stockAmount) {
        this.stockCount = stockCount;
        this.stockAmount = stockAmount;
    }
}
