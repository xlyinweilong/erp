package com.yin.erp.bill.delivery.entity.po;

import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 配货单货品
 *
 * @author yin
 */
@Entity
@Table(name = "bill_delivery_goods")
@Getter
@Setter
public class DeliveryGoodsPo extends BillGoodsPo {

}
