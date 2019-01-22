package com.yin.erp.info.channel.dao;


import com.yin.erp.info.channel.entity.po.ChannelPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;

/**
 * 渠道
 *
 * @author yin
 */
@Resource
public interface ChannelDao extends JpaRepository<ChannelPo, String>, JpaSpecificationExecutor {
    ChannelPo findByCode(String code);
}
