package com.yin.erp.bill.noticechannel2channelout.dao;


import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.channel.BaseBillToChannelDao;
import com.yin.erp.bill.noticechannel2channelout.entity.po.NoticeChannel2ChannelOutPo;

import javax.annotation.Resource;


/**
 * 通知单-店铺调出
 *
 * @author yin
 */
@Resource
public interface NoticeChannel2ChannelOutDao extends BaseBillChannelDao<NoticeChannel2ChannelOutPo, String>,BaseBillToChannelDao<NoticeChannel2ChannelOutPo, String> {

}