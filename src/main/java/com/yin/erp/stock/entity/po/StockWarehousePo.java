package com.yin.erp.stock.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 仓库库存
 *
 * @author yin.weilong
 * @date 2018.12.21
 */
@Entity
@Table(name = "stock_warehouse")
@Getter
@Setter
public class StockWarehousePo extends BasePo {

    @Column(name = "warehouse_id")
    private String warehouseId;

    @Column(name = "warehouse_code")
    private String warehouseCode;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "goods_id")
    private String goodsId;

    @Column(name = "goods_code")
    private String goodsCode;

    @Column(name = "goods_name")
    private String goodsName;

    @Column(name = "goods_color_id")
    private String goodsColorId;

    @Column(name = "goods_color_code")
    private String goodsColorCode;

    @Column(name = "goods_color_name")
    private String goodsColorName;

    @Column(name = "goods_in_size_id")
    private String goodsInSizeId;

    @Column(name = "goods_in_size_name")
    private String goodsInSizeName;

    @Column(name = "goods_size_id")
    private String goodsSizeId;

    @Column(name = "goods_size_name")
    private String goodsSizeName;

    @Column(name = "stock_count")
    private Integer stockCount = 0;
}
