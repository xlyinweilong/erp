package com.yin.erp.vip.xp.dao;


import com.yin.erp.vip.xp.entity.po.VipXpUpRuleGoodsPo;
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
public interface VipXpUpRuleGoodsDao extends JpaRepository<VipXpUpRuleGoodsPo, String>, JpaSpecificationExecutor {


    @Modifying
    @Query("delete from VipXpUpRuleGoodsPo t where t.vipXpUpRuleId = :vipXpUpRuleId")
    int deleteAllByRuleId(String vipXpUpRuleId);
}
