package com.yin.erp.bill.channelinventory.entity.po;

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
 * 店铺调出
 *
 * @author yin
 */
@Entity
@Table(name = "bill_channel_inventory", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class ChannelInventoryPo extends BillPo {

    @Column(name = "parent_bill_id")
    private String parentBillId;

    @Column(name = "parent_bill_code")
    private String parentBillCode;

    /**
     * 渠道
     */
    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_code")
    private String channelCode;

    /**
     * 盘次
     */
    @Column(name = "times")
    private Integer times;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new ChannelInventoryGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new ChannelInventoryDetailPo();
    }

}
