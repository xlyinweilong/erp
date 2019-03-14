package com.yin.erp.activity.dao;


import com.yin.erp.activity.entity.po.ActivityVipPo;
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
public interface ActivityVipDao extends JpaRepository<ActivityVipPo, String>, JpaSpecificationExecutor {

    int deleteAllByActivityId(String activityId);

    List<ActivityVipPo> findByActivityId(String activityId);

    /**
     * 等级数量
     *
     * @param gradeId
     * @return
     */
    Long countByGradeId(String gradeId);

}
