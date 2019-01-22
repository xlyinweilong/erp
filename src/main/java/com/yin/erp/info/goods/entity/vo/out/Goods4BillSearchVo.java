package com.yin.erp.info.goods.entity.vo.out;

import com.yin.erp.info.goods.entity.vo.GoodsVo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 单据查询货品
 *
 * @author yin.weilong
 * @date 2018.12.13
 */
@Getter
@Setter
public class Goods4BillSearchVo extends GoodsVo {

    private BigDecimal tagPrice;

    private BigDecimal price;

    private Integer stockCount;
}
