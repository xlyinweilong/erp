package com.yin.erp.bill.channel2supplier.entity.po;

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
 * 仓库出货
 *
 * @author yin
 */
@Entity
@Table(name = "bill_channel_2_supplier", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class Channel2SupplierPo extends BillPo {

    /**
     * 仓库
     */
    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_code")
    private String channelCode;

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
        return new Channel2SupplierGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new Channel2SupplierDetailPo();
    }

}
