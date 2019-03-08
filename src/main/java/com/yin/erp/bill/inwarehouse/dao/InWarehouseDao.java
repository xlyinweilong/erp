package com.yin.erp.bill.inwarehouse.dao;


import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.bill.inwarehouse.entity.po.InWarehousePo;

import javax.annotation.Resource;


/**
 * 渠道收货
 *
 * @author yin
 */
@Resource
public interface InWarehouseDao extends BaseBillWarehouseDao<InWarehousePo, String>,BaseBillChannelDao<InWarehousePo, String> {

}
