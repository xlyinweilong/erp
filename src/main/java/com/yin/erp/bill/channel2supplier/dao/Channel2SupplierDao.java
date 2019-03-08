package com.yin.erp.bill.channel2supplier.dao;


import com.yin.erp.bill.channel2supplier.entity.po.Channel2SupplierPo;
import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.supplier.BaseBillSupplierDao;

import javax.annotation.Resource;


/**
 * 仓库出货
 *
 * @author yin
 */
@Resource
public interface Channel2SupplierDao extends BaseBillChannelDao<Channel2SupplierPo, String>, BaseBillSupplierDao<Channel2SupplierPo, String> {

}
