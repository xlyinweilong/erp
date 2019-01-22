package com.yin.erp.config.sysconfig.dao;


import com.yin.erp.config.sysconfig.entity.po.ConfigChannelPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 渠道配置
 *
 * @author yin
 */
@Resource
public interface ConfigChannelDao extends JpaRepository<ConfigChannelPo, String>, JpaSpecificationExecutor {

    /**
     * 删除渠道的所有
     *
     * @param channelId
     * @return
     */
    @Modifying
    @Query("delete from ConfigChannelPo t where t.channelId = :channelId")
    int deleteAllByChannelId(@Param("channelId") String channelId);

    /**
     * 查询通过渠道ID
     *
     * @param channelId
     * @return
     */
    List<ConfigChannelPo> findByChannelId(String channelId);

    /**
     * 查询获取通过渠道ID和配置ID
     *
     * @param channelId
     * @param configId
     * @return
     */
    ConfigChannelPo findByChannelIdAndConfigId(String channelId, String configId);
}
