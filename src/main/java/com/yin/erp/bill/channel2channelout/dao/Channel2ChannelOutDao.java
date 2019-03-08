package com.yin.erp.bill.channel2channelout.dao;


import com.yin.erp.bill.channel2channelout.entity.po.Channel2ChannelOutPo;
import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.channel.BaseBillToChannelDao;

import javax.annotation.Resource;


/**
 * 店铺调出
 *
 * @author yin
 */
@Resource
public interface Channel2ChannelOutDao extends BaseBillChannelDao<Channel2ChannelOutPo, String>,BaseBillToChannelDao<Channel2ChannelOutPo, String> {

}
