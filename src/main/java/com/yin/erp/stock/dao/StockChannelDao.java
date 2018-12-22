package com.yin.erp.stock.dao;


import com.yin.erp.stock.entity.po.StockChannelPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Resource;

/**
 * 渠道库存
 *
 * @author yin
 */
@Resource
public interface StockChannelDao extends PagingAndSortingRepository<StockChannelPo, String>, JpaSpecificationExecutor {

    /**
     * 根据数据查询
     *
     * @param channelId
     * @param goodsId
     * @param goodsColorId
     * @param goodsInSizeId
     * @param goodsSizeId
     * @return
     */
    StockChannelPo findByChannelIdAndGoodsIdAndGoodsColorIdAndGoodsInSizeIdAndGoodsSizeId(String channelId, String goodsId, String goodsColorId, String goodsInSizeId, String goodsSizeId);

}
