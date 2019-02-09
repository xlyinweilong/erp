package com.yin.erp.activity.dao;


import com.yin.erp.activity.entity.po.ActivityChannelPo;
import com.yin.erp.activity.entity.po.ActivityGoodsPo;
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
public interface ActivityGoodsDao extends JpaRepository<ActivityGoodsPo, String>, JpaSpecificationExecutor {

    int deleteAllByActivityId(String activityId);

    List<ActivityGoodsPo> findByActivityId(String activityId);

}
