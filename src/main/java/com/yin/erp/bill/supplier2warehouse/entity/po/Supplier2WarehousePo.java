package com.yin.erp.bill.supplier2warehouse.entity.po;

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
 * 厂家来货
 *
 * @author yin
 */
@Entity
@Table(name = "bill_supplier_2_warehouse", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class Supplier2WarehousePo extends BillPo {

    /**
     * 上级单据
     */
    @Column(name = "parent_bill_id")
    private String parentBillId;

    @Column(name = "parent_bill_code")
    private String parentBillCode;

    /**
     * 供应商
     */
    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_code")
    private String supplierCode;

    /**
     * 仓库
     */
    @Column(name = "warehouse_id")
    private String warehouseId;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "warehouse_code")
    private String warehouseCode;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new Supplier2WarehouseGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new Supplier2WarehouseDetailPo();
    }
}
