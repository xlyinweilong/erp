package com.yin.erp.bill.purchase.entity.po;

import com.yin.erp.bill.common.entity.po.BillDetailQuotedPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 采购单详情
 *
 * @author yin
 */
@Entity
@Table(name = "bill_purchase_detail")
@Getter
@Setter
public class PurchaseDetailPo extends BillDetailQuotedPo {

}
