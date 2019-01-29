package com.yin.erp.vip.integral.dao;


import com.yin.erp.vip.integral.entity.po.VipIntegralToAmountPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 会员积分兑换金额
 *
 * @author yin
 */
@Repository
public interface VipIntegralToAmountDao extends JpaRepository<VipIntegralToAmountPo, String>, JpaSpecificationExecutor {

}
