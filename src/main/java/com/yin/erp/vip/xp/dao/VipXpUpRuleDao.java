package com.yin.erp.vip.xp.dao;


import com.yin.erp.vip.xp.entity.po.VipXpUpRulePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 会员积分获取规则
 *
 * @author yin
 */
@Repository
public interface VipXpUpRuleDao extends JpaRepository<VipXpUpRulePo, String>, JpaSpecificationExecutor {

}
