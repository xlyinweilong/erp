package com.yin.erp.config.sysconfig.dao;


import com.yin.erp.config.sysconfig.entity.po.ConfigWarehousePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 仓库配置
 *
 * @author yin
 */
@Resource
public interface ConfigWarehouseDao extends JpaRepository<ConfigWarehousePo, String>, JpaSpecificationExecutor {

    /**
     * 删除渠道的所有
     *
     * @param warehouseId
     * @return
     */
    @Modifying
    @Query("delete from ConfigWarehousePo t where t.warehouseId = :warehouseId")
    int deleteAllByWarehouseId(@Param("warehouseId") String warehouseId);

    /**
     * 查询通过渠道ID
     *
     * @param warehouseId
     * @return
     */
    List<ConfigWarehousePo> findByWarehouseId(String warehouseId);



    /**
     * 查询获取通过仓库ID和配置ID
     *
     * @param warehouseId
     * @param configId
     * @return
     */
    ConfigWarehousePo findByWarehouseIdAndConfigId(String warehouseId, String configId);
}
