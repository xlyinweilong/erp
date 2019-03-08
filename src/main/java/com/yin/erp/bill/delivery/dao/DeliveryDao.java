package com.yin.erp.bill.delivery.dao;


import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.bill.delivery.entity.po.DeliveryPo;

import javax.annotation.Resource;


/**
 * 配货单
 *
 * @author yin
 */
@Resource
public interface DeliveryDao extends BaseBillChannelDao<DeliveryPo, String>,BaseBillWarehouseDao<DeliveryPo, String> {

}
