package com.yin.erp.bill.purchase.entity.po;

import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillQuotedPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 采购单
 *
 * @author yin
 */
@Entity
@Table(name = "bill_purchase")
@Getter
@Setter
public class PurchasePo extends BillQuotedPo {

    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "supplier_code")
    private String supplierCode;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new PurchaseGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new PurchaseDetailPo();
    }

}
