package com.yin.erp.bill.order.entity.po;

import com.yin.erp.bill.common.entity.po.BillDetailQuotedPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 订货单详情
 *
 * @author yin
 */
@Entity
@Table(name = "bill_order_detail")
@Getter
@Setter
public class OrderDetailPo extends BillDetailQuotedPo {


}
