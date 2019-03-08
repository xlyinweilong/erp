package com.yin.erp.bill.order.entity.po;

import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 订货单货品
 *
 * @author yin
 */
@Entity
@Table(name = "bill_order_goods")
@Getter
@Setter
public class OrderGoodsPo extends BillGoodsPo {

}
