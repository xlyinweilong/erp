package com.yin.erp.bill.purchase.entity.po;

import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 采购单货品
 *
 * @author yin
 */
@Entity
@Table(name = "bill_purchase_goods")
@Getter
@Setter
public class PurchaseGoodsPo extends BillGoodsPo {

}
