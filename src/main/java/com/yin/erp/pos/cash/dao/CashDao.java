package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.CashPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 销售
 * @author yin
 */
@Repository
public interface CashDao extends JpaRepository<CashPo, String>, JpaSpecificationExecutor {

}
