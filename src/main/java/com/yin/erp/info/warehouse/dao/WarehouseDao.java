package com.yin.erp.info.warehouse.dao;


import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;

/**
 * 仓库
 *
 * @author yin
 */
@Resource
public interface WarehouseDao extends JpaRepository<WarehousePo, String>, JpaSpecificationExecutor {
    WarehousePo findByCode(String code);

    @Query("select count(t.id) from WarehousePo t where t.groupId = :groupId")
    Long countByGourpId(@Param("groupId") String groupId);
}
