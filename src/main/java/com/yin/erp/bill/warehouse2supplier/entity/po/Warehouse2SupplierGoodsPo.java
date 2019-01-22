package com.yin.erp.bill.warehouse2supplier.entity.po;

import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 单据货品
 *
 * @author yin
 */
@Entity
@Table(name = "bill_warehouse_2_supplier_goods")
@Getter
@Setter
public class Warehouse2SupplierGoodsPo extends BillGoodsPo {


}
