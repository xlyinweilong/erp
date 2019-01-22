package com.yin.erp.user.role.dao;


import com.yin.erp.user.role.entity.po.PowerPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 用户仓库
 */
@Repository
public interface PowerDao extends JpaRepository<PowerPo, String>, JpaSpecificationExecutor {

}
