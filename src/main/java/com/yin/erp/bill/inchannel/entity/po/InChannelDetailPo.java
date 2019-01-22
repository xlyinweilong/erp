package com.yin.erp.bill.inchannel.entity.po;

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
@Table(name = "bill_in_channel_detail")
@Getter
@Setter
public class InChannelDetailPo extends BillDetailPo {


}
