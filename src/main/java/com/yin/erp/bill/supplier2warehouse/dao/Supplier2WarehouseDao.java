package com.yin.erp.bill.supplier2warehouse.dao;


import com.yin.erp.bill.common.dao.supplier.BaseBillSupplierDao;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehousePo;

import javax.annotation.Resource;


/**
 * 厂家来货
 *
 * @author yin
 */
@Resource
public interface Supplier2WarehouseDao extends BaseBillSupplierDao<Supplier2WarehousePo, String>, BaseBillWarehouseDao<Supplier2WarehousePo, String> {

}
