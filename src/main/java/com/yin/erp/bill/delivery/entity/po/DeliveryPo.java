package com.yin.erp.bill.delivery.entity.po;

import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 配货单
 *
 * @author yin
 */
@Entity
@Table(name = "bill_delivery")
@Getter
@Setter
public class DeliveryPo extends BillPo {

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

    @Column(name = "child_bill_id")
    private String childBillId;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new DeliveryGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new DeliveryDetailPo();
    }

}
