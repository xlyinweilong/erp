package com.yin.erp.activity.dao;


import com.yin.erp.activity.entity.po.ActivityChannelPo;
import com.yin.erp.activity.entity.po.ActivityRulePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 促销规则
 *
 * @author yin
 */
@Repository
public interface ActivityRuleDao extends JpaRepository<ActivityRulePo, String>, JpaSpecificationExecutor {

    int deleteAllByActivityId(String activityId);

    List<ActivityRulePo> findByActivityId(String activityId);

}
