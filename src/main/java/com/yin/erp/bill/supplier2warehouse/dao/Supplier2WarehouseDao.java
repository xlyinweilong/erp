package com.yin.erp.bill.supplier2warehouse.dao;


import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehousePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;


/**
 * 厂家来货
 *
 * @author yin
 */
@Resource
public interface Supplier2WarehouseDao extends JpaRepository<Supplier2WarehousePo, String>, JpaSpecificationExecutor {

}
