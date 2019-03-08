package com.yin.erp.bill.purchase.dao;


import com.yin.erp.bill.common.dao.BaseBillDao;
import com.yin.erp.bill.purchase.entity.po.PurchasePo;

import javax.annotation.Resource;


/**
 * 采购单
 *
 * @author yin
 */
@Resource
public interface PurchaseDao extends BaseBillDao<PurchasePo, String> {

}
