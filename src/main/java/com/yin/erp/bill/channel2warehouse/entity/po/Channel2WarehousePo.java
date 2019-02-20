package com.yin.erp.bill.channel2warehouse.entity.po;

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
@Table(name = "bill_channel_2_warehouse", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class Channel2WarehousePo extends BillPo {

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
    @Column(name = "warehouse_id")
    private String warehouseId;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "warehouse_code")
    private String warehouseCode;

    @Column(name = "child_bill_id")
    private String childBillId;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new Channel2WarehouseGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new Channel2WarehouseDetailPo();
    }

}
