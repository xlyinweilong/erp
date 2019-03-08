package com.yin.erp.bill.delivery.entity.po;

import com.yin.erp.bill.common.entity.po.BillDetailPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 配货单详情
 *
 * @author yin
 */
@Entity
@Table(name = "bill_delivery_detail")
@Getter
@Setter
public class DeliveryDetailPo extends BillDetailPo {


}
