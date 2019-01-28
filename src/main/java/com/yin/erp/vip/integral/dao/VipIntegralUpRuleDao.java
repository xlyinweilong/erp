package com.yin.erp.vip.integral.dao;


import com.yin.erp.vip.integral.entity.po.VipIntegralUpRulePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 会员积分获取规则
 *
 * @author yin
 */
@Repository
public interface VipIntegralUpRuleDao extends JpaRepository<VipIntegralUpRulePo, String>, JpaSpecificationExecutor {

}
