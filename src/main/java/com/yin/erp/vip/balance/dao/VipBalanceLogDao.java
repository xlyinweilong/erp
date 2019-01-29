package com.yin.erp.vip.balance.dao;


import com.yin.erp.vip.balance.entity.po.VipBalanceLogPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 会员余额日志
 *
 * @author yin
 */
@Repository
public interface VipBalanceLogDao extends JpaRepository<VipBalanceLogPo, String>, JpaSpecificationExecutor {

}
