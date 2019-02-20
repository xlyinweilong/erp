package com.yin.erp.vip.integral.dao;


import com.yin.erp.vip.integral.entity.po.VipIntegralRuleGoodsPo;
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
public interface VipIntegralRuleGoodsDao extends JpaRepository<VipIntegralRuleGoodsPo, String>, JpaSpecificationExecutor {


    @Modifying
    @Query("delete from VipIntegralRuleGoodsPo t where t.vipIntegralUpRuleId = :vipIntegralUpRuleId")
    int deleteAllByRuleId(String vipIntegralUpRuleId);
}
