package com.yin.erp.bill.warehouse2channel.dao;


import com.yin.erp.bill.common.dao.BaseBillDao;
import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelPo;

import javax.annotation.Resource;


/**
 * 仓库出货
 *
 * @author yin
 */
@Resource
public interface Warehouse2ChannelDao extends BaseBillDao<Warehouse2ChannelPo, String> {

    @Override
    Warehouse2ChannelPo findByCode(String code);
}
