package com.yin.erp.stock.dao;


import com.yin.erp.stock.entity.po.BaseStockPo;
import com.yin.erp.stock.entity.po.StockChannelPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;
import java.util.List;

/**
 * 渠道库存
 *
 * @author yin
 */
@Resource
public interface StockChannelDao extends JpaRepository<StockChannelPo, String>, JpaSpecificationExecutor {

    /**
     * 根据数据查询
     *
     * @param channelId
     * @param goodsId
     * @param goodsColorId
     * @param goodsSizeId
     * @return
     */
    StockChannelPo findByChannelIdAndGoodsIdAndGoodsColorIdAndGoodsSizeId(String channelId, String goodsId, String goodsColorId, String goodsSizeId);


    /**
     * 查询某渠道得全部库存
     *
     * @param channelId
     * @return
     */
    List<BaseStockPo> findAllByChannelId(String channelId);

}
