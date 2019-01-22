package com.yin.erp.bill.supplier2channel.entity.po;

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
@Table(name = "bill_supplier_2_channel", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class Supplier2ChannelPo extends BillPo {

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
     * 渠道
     */
    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_code")
    private String channelCode;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new Supplier2ChannelGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new Supplier2ChannelDetailPo();
    }

}
