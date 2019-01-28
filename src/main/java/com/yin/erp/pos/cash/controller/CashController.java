package com.yin.erp.pos.cash.controller;

import com.yin.erp.info.barcode.service.BarCodeService;
import com.yin.erp.info.goods.service.GoodsService;
import com.yin.erp.pos.cash.service.CashService;
import com.yin.erp.stock.service.StockChannelService;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 销售控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/pos/cash")
@Transactional(rollbackFor = Throwable.class)
public class CashController {

    @Autowired
    private CashService cashService;
    @Autowired
    private BarCodeService barCodeService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private StockChannelService stockChannelService;



    //查询销售


}
