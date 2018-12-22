package com.yin.erp.stock.dao;


import com.yin.erp.stock.entity.po.StockWarehousePo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Resource;

/**
 * 仓库库存
 *
 * @author yin
 */
@Resource
public interface StockWarehouseDao extends PagingAndSortingRepository<StockWarehousePo, String>, JpaSpecificationExecutor {


    /**
     * 根据条件查询库存
     *
     * @param warehouseId
     * @param goodsId
     * @param goodsColorId
     * @param goodsInSizeId
     * @param goodsSizeId
     * @return
     */
    StockWarehousePo findByWarehouseIdAndGoodsIdAndGoodsColorIdAndGoodsInSizeIdAndGoodsSizeId(String warehouseId, String goodsId, String goodsColorId, String goodsInSizeId, String goodsSizeId);
}
