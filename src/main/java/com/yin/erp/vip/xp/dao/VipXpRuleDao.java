package com.yin.erp.vip.xp.dao;


import com.yin.erp.vip.xp.entity.po.VipXpRulePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 会员积分获取规则
 *
 * @author yin
 */
@Repository
public interface VipXpRuleDao extends JpaRepository<VipXpRulePo, String>, JpaSpecificationExecutor {

    /**
     * 查询等级的数量
     *
     * @param vipGradeId
     * @return
     */
    Long countByVipGradeId(String vipGradeId);

    /**
     * 查询当前能用的经验规则
     *
     * @param vipGradeId
     * @param now
     * @return
     */
    @Query("select t from VipXpRulePo t where t.vipGradeId = :vipGradeId and t.startDate <= :now and (t.endDate is null or t.endDate >= :now) order by t.priority desc")
    List<VipXpRulePo> findAllCanUse(@Param("vipGradeId") String vipGradeId, @Param("now") Date now);
}
