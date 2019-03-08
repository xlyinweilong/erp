package com.yin.erp.stock.entity.po;

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
public class StockWarehousePo extends BaseStockPo {

    @Column(name = "warehouse_id")
    private String warehouseId;

    @Column(name = "warehouse_code")
    private String warehouseCode;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "warehouse_group_id")
    private String warehouseGroupId;

}
