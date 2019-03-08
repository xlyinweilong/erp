package com.yin.erp.bill.noticechannel2channelout.entity.po;

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
 * 通知单-店铺调出
 *
 * @author yin
 */
@Entity
@Table(name = "bill_notice_channel_2_channel_out", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class NoticeChannel2ChannelOutPo extends BillPo {

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

    @Column(name = "child_bill_id")
    private String childBillId;


    @Override
    public BillGoodsPo getBillGoodsInstance() {
        return new NoticeChannel2ChannelOutGoodsPo();
    }

    @Override
    public BillDetailPo getBillDetailInstance() {
        return new NoticeChannel2ChannelOutDetailPo();
    }

}
