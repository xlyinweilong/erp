package com.yin.erp.bill.supplier2channel.dao;


import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.supplier.BaseBillSupplierDao;
import com.yin.erp.bill.supplier2channel.entity.po.Supplier2ChannelPo;

import javax.annotation.Resource;


/**
 * 渠道采购收货
 *
 * @author yin
 */
@Resource
public interface Supplier2ChannelDao extends BaseBillChannelDao<Supplier2ChannelPo, String>,BaseBillSupplierDao<Supplier2ChannelPo, String> {

}
