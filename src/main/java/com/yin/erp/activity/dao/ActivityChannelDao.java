package com.yin.erp.activity.dao;


import com.yin.erp.activity.entity.po.ActivityChannelPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 促销活动
 *
 * @author yin
 */
@Repository
public interface ActivityChannelDao extends JpaRepository<ActivityChannelPo, String>, JpaSpecificationExecutor {

    int deleteAllByActivityId(String activityId);

    List<ActivityChannelPo> findByActivityId(String activityId);

    @Query("select t.activityId from ActivityChannelPo t where t.channelId = :channelId")
    List<String> findDistinctActivityIdByChannelId(@Param("channelId") String channelId);

}
