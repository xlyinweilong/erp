package com.yin.erp.info.channel.dao;


import com.yin.erp.info.channel.entity.po.ChannelPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;

/**
 * 渠道
 *
 * @author yin
 */
@Resource
public interface ChannelDao extends JpaRepository<ChannelPo, String>, JpaSpecificationExecutor {
    ChannelPo findByCode(String code);

    @Query("select count(t.id) from ChannelPo t where t.groupId = :groupId")
    Long countByGourpId(@Param("groupId") String groupId);
}
