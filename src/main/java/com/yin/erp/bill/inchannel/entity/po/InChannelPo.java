package com.yin.erp.bill.inchannel.entity.po;

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
 * 渠道收货
 *
 * @author yin
 */
@Entity
@Table(name = "bill_in_channel", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class InChannelPo extends BillPo {

    /**
     * 上级单据
     */
    @Column(name = "parent_bill_id")
    private String parentBillId;

    @Column(name = "parent_bill_code")
    private String parentBillCode;

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
    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_code")
    private String channelCode;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new InChannelGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new InChannelDetailPo();
    }

}
