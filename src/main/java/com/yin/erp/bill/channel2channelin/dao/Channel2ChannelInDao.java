package com.yin.erp.bill.channel2channelin.dao;


import com.yin.erp.bill.channel2channelin.entity.po.Channel2ChannelInPo;
import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.channel.BaseBillToChannelDao;

import javax.annotation.Resource;


/**
 * 店铺调出
 *
 * @author yin
 */
@Resource
public interface Channel2ChannelInDao extends BaseBillChannelDao<Channel2ChannelInPo, String>,BaseBillToChannelDao<Channel2ChannelInPo, String> {

}
