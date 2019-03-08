package com.yin.erp.bill.order.dao;


import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.bill.order.entity.po.OrderPo;

import javax.annotation.Resource;


/**
 * 订货单
 *
 * @author yin
 */
@Resource
public interface OrderDao extends BaseBillChannelDao<OrderPo, String>,BaseBillWarehouseDao<OrderPo, String> {

}
