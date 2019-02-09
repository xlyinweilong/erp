package com.yin.erp.activity.dao;


import com.yin.erp.activity.entity.po.ActivityPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 促销活动
 *
 * @author yin
 */
@Repository
public interface ActivityDao extends JpaRepository<ActivityPo, String>, JpaSpecificationExecutor {

}
