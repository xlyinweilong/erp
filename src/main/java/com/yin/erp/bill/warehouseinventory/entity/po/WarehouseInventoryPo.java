package com.yin.erp.bill.warehouseinventory.entity.po;

import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 仓库损益
 *
 * @author yin
 */
@Entity
@Table(name = "bill_warehouse_inventory", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class WarehouseInventoryPo extends BillPo {

    /**
     * 渠道
     */
    @Column(name = "warehouse_id")
    private String warehouseId;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "warehouse_code")
    private String warehouseCode;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new WarehouseInventoryGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new WarehouseInventoryDetailPo();
    }

}
