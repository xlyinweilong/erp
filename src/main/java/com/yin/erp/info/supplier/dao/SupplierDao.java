package com.yin.erp.info.supplier.dao;


import com.yin.erp.info.supplier.entity.po.SupplierPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;

/**
 * 供应商
 *
 * @author yin
 */
@Resource
public interface SupplierDao extends JpaRepository<SupplierPo, String>, JpaSpecificationExecutor {
    SupplierPo findByCode(String code);

    @Query("select count(t.id) from SupplierPo t where t.groupId = :groupId")
    Long countByGourpId(@Param("groupId") String groupId);
}
