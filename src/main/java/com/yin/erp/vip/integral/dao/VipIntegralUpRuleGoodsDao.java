package com.yin.erp.vip.integral.dao;


import com.yin.erp.vip.integral.entity.po.VipIntegralUpRuleGoodsPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 会员积分获取规则对应的货品
 *
 * @author yin
 */
@Repository
public interface VipIntegralUpRuleGoodsDao extends JpaRepository<VipIntegralUpRuleGoodsPo, String>, JpaSpecificationExecutor {


    @Modifying
    @Query("delete from VipIntegralUpRuleGoodsPo t where t.vipIntegralUpRuleId = :vipIntegralUpRuleId")
    int deleteAllByRuleId(String vipIntegralUpRuleId);
}
