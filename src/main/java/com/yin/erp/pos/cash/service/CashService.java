package com.yin.erp.pos.cash.service;

import com.yin.erp.pos.cash.dao.CashDao;
import com.yin.erp.pos.cash.dao.CashGoodsDao;
import com.yin.erp.pos.cash.dao.CashPaymentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 销售服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class CashService {

    @Autowired
    private CashDao cashDao;
    @Autowired
    private CashGoodsDao cashGoodsDao;
    @Autowired
    private CashPaymentDao cashPaymentDao;

}
