package com.yin.erp.bill.channel2supplier.entity.po;

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
@Table(name = "bill_channel_2_supplier_goods")
@Getter
@Setter
public class Channel2SupplierGoodsPo extends BillGoodsPo {

}
