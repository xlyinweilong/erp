package com.yin.erp.bill.channel2channelout.entity.po;

import com.yin.erp.bill.common.entity.po.BillDetailPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 单据详情
 *
 * @author yin
 */
@Entity
@Table(name = "bill_channel_2_channel_out_detail")
@Getter
@Setter
public class Channel2ChannelOutDetailPo extends BillDetailPo {


}
