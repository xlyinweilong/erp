package com.yin.erp.bill.supplier2warehouse.entity.po;

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
@Table(name = "bill_supplier_2_warehouse_detail")
@Getter
@Setter
public class Supplier2WarehouseDetailPo extends BillDetailPo {


}
