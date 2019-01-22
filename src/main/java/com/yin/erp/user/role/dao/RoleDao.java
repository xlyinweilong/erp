package com.yin.erp.user.role.dao;


import com.yin.erp.user.role.entity.po.RolePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * 用户仓库
 */
@org.springframework.transaction.annotation.Transactional(rollbackFor = Throwable.class)
@Repository
public interface RoleDao extends JpaRepository<RolePo, String>, JpaSpecificationExecutor {

}
