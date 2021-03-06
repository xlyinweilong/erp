package com.yin.erp.vip.grade.dao;


import com.yin.erp.vip.grade.entity.po.VipGradePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员等级
 *
 * @author yin
 */
@Repository
public interface VipGradeDao extends JpaRepository<VipGradePo, String>, JpaSpecificationExecutor {

    /**
     * 清除所有深度
     *
     * @return
     */
    @Modifying
    @Query("update VipGradePo t set t.indexDepth = null,t.defaultGrade = false,t.lowestXpValue = null")
    int updateAllIndexDepthNull();


    /**
     * 清除所有默认等级
     *
     * @return
     */
    @Modifying
    @Query("update VipGradePo t set t.defaultGrade = false")
    int updateAllDefaultGradeFalse();


    /**
     * 查询设置的会员等级
     *
     * @return
     */
    @Query("select t from VipGradePo t where t.indexDepth is not null order by t.indexDepth asc")
    List<VipGradePo> findAllCanUserd();
}
