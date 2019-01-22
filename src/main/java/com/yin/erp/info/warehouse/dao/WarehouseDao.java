package com.yin.erp.info.warehouse.dao;


import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;

/**
 * 仓库
 *
 * @author yin
 */
@Resource
public interface WarehouseDao extends JpaRepository<WarehousePo, String>, JpaSpecificationExecutor {
    WarehousePo findByCode(String code);
}
