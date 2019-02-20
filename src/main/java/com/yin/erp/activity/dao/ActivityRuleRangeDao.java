package com.yin.erp.activity.dao;


import com.yin.erp.activity.entity.po.ActivityRuleRangePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 促销活动
 *
 * @author yin
 */
@Repository
public interface ActivityRuleRangeDao extends JpaRepository<ActivityRuleRangePo, String>, JpaSpecificationExecutor {

    int deleteAllByActivityId(String activityId);

    List<ActivityRuleRangePo> findByActivityId(String activityId);

}
