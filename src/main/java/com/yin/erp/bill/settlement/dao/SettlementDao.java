package com.yin.erp.bill.settlement.dao;


import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.bill.settlement.entity.po.SettlementPo;

import javax.annotation.Resource;


/**
 * 结存
 *
 * @author yin
 */
@Resource
public interface SettlementDao extends BaseBillWarehouseDao<SettlementPo, String>,BaseBillChannelDao<SettlementPo, String> {

}
