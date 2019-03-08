package com.yin.erp.bill.channel2warehouse.dao;


import com.yin.erp.bill.channel2warehouse.entity.po.Channel2WarehousePo;
import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;

import javax.annotation.Resource;


/**
 * 仓库出货
 *
 * @author yin
 */
@Resource
public interface Channel2WarehouseDao extends BaseBillChannelDao<Channel2WarehousePo, String>,BaseBillWarehouseDao<Channel2WarehousePo, String> {

    Channel2WarehousePo findByCode(String code);
}
