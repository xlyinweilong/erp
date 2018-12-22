package com.yin.erp.bill.supplier2warehouse.entity.po;

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
@Table(name = "bill_supplier_2_warehouse_goods")
@Getter
@Setter
public class Supplier2WarehouseGoodsPo extends BillGoodsPo {


}
