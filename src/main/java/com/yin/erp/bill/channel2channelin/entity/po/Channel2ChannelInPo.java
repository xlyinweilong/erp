package com.yin.erp.bill.channel2channelin.entity.po;

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
@Table(name = "bill_channel_2_channel_in", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class Channel2ChannelInPo extends BillPo {

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
     * 渠道
     */
    @Column(name = "to_channel_id")
    private String toChannelId;

    @Column(name = "to_channel_name")
    private String toChannelName;

    @Column(name = "to_channel_code")
    private String toChannelCode;

    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new Channel2ChannelInGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new Channel2ChannelInDetailPo();
    }

}
