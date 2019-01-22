package com.yin.erp.info.supplier.dao;


import com.yin.erp.info.supplier.entity.po.SupplierPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;

/**
 * 供应商
 *
 * @author yin
 */
@Resource
public interface SupplierDao extends JpaRepository<SupplierPo, String>, JpaSpecificationExecutor {
    SupplierPo findByCode(String code);
}
