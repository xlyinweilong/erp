package com.yin.erp.bill.warehouse2supplier.entity.po;

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
@Table(name = "bill_warehouse_2_supplier_detail")
@Getter
@Setter
public class Warehouse2SupplierDetailPo extends BillDetailPo {


}
