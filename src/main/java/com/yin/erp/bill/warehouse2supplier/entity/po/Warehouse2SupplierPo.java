package com.yin.erp.bill.warehouse2supplier.entity.po;

import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.supplier2channel.entity.po.Supplier2ChannelDetailPo;
import com.yin.erp.bill.supplier2channel.entity.po.Supplier2ChannelGoodsPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 仓库出货
 *
 * @author yin
 */
@Entity
@Table(name = "bill_warehouse_2_supplier", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class Warehouse2SupplierPo extends BillPo {

    /**
     * 仓库
     */
    @Column(name = "warehouse_id")
    private String warehouseId;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 渠道
     */
    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_code")
    private String supplierCode;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new Warehouse2SupplierGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new Warehouse2SupplierDetailPo();
    }

}
