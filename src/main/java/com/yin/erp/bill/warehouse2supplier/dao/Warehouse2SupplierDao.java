package com.yin.erp.bill.warehouse2supplier.dao;


import com.yin.erp.bill.common.dao.supplier.BaseBillSupplierDao;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.bill.warehouse2supplier.entity.po.Warehouse2SupplierPo;

import javax.annotation.Resource;


/**
 * 仓库出货
 *
 * @author yin
 */
@Resource
public interface Warehouse2SupplierDao extends BaseBillSupplierDao<Warehouse2SupplierPo, String>,BaseBillWarehouseDao<Warehouse2SupplierPo, String> {

}
