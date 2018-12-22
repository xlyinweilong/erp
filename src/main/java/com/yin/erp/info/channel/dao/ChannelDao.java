package com.yin.erp.info.channel.dao;


import com.yin.erp.info.channel.entity.po.ChannelPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Resource;

/**
 * 渠道
 *
 * @author yin
 */
@Resource
public interface ChannelDao extends PagingAndSortingRepository<ChannelPo, String>, JpaSpecificationExecutor {

}
