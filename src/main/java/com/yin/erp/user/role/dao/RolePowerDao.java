package com.yin.erp.user.role.dao;


import com.yin.erp.user.role.entity.po.RolePowerPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户权利
 */
@org.springframework.transaction.annotation.Transactional(rollbackFor = Throwable.class)
@Repository
public interface RolePowerDao extends JpaRepository<RolePowerPo, String>, JpaSpecificationExecutor {

    @Modifying
    @Query("delete from RolePowerPo t where t.roleId = :roleId")
    int deleteAllByRoleId(@Param("roleId") String roleId);

    @Query("SELECT t.powerId from RolePowerPo t where t.roleId = :roleId")
    List<String> findPowerIdByRoleId(@Param("roleId") String roleId);

}
