package com.yin.erp.bill.warehouse2channel.dao;


import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Resource;


/**
 * 仓库出货
 *
 * @author yin
 */
@Resource
public interface Warehouse2ChannelDao extends PagingAndSortingRepository<Warehouse2ChannelPo, String>, JpaSpecificationExecutor {

}
