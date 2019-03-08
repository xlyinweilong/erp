package com.yin.erp.bill.inchannel.dao;


import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.bill.inchannel.entity.po.InChannelPo;

import javax.annotation.Resource;


/**
 * 渠道收货
 *
 * @author yin
 */
@Resource
public interface InChannelDao extends BaseBillChannelDao<InChannelPo, String>,BaseBillWarehouseDao<InChannelPo, String> {

}
