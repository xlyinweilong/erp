package com.yin.erp.stock.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.info.channel.entity.vo.ChannelVo;
import com.yin.erp.stock.service.StockChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 渠道制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/stock/channel")
public class StockChannelController {

    @Autowired
    private StockChannelService stockChannelService;


    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody ChannelVo vo) throws Exception {
        return BaseJson.getSuccess();
    }

    /**
     * 渠道库存详情
     *
     * @return
     */
    @GetMapping(value = "stock_info")
    public BaseJson channelStockInfo(String channelId, String goodsId, String goodsColorId, String goodsSizeId) {
        return BaseJson.getSuccess(stockChannelService.stockCount(channelId, goodsId, goodsColorId, goodsSizeId));
    }


}
