package com.yin.erp.vip.balance.dao;


import com.yin.erp.vip.balance.entity.po.VipBalanceAddPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 会员余额充值
 *
 * @author yin
 */
@Repository
public interface VipBalanceAddDao extends JpaRepository<VipBalanceAddPo, String>, JpaSpecificationExecutor {

}
