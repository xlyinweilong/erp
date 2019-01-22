package com.yin.erp.user.role.dao;


import com.yin.erp.user.role.entity.po.RoleGoodsGroupPo;
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
public interface RoleGoodsGroupDao extends JpaRepository<RoleGoodsGroupPo, String>, JpaSpecificationExecutor {

    @Modifying
    @Query("delete from RoleGoodsGroupPo t where t.roleId = :roleId")
    int deleteAllByRoleId(@Param("roleId") String roleId);

    @Query("SELECT t.goodsGroupId from RoleGoodsGroupPo t where t.roleId = :roleId")
    List<String> findGoodsGroupIdByRoleId(@Param("roleId") String roleId);

}
