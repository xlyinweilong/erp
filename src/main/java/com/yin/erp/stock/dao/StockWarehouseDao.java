package com.yin.erp.stock.dao;


import com.yin.erp.stock.entity.po.BaseStockPo;
import com.yin.erp.stock.entity.po.StockWarehousePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;
import java.util.List;

/**
 * 仓库库存
 *
 * @author yin
 */
@Resource
public interface StockWarehouseDao extends JpaRepository<StockWarehousePo, String>, JpaSpecificationExecutor {


    /**
     * 根据条件查询库存
     *
     * @param warehouseId
     * @param goodsId
     * @param goodsColorId
     * @param goodsSizeId
     * @return
     */
    StockWarehousePo findByWarehouseIdAndGoodsIdAndGoodsColorIdAndGoodsSizeId(String warehouseId, String goodsId, String goodsColorId, String goodsSizeId);


    /**
     * 查询某仓库得全部库存
     *
     * @param warehouseId
     * @return
     */
    List<BaseStockPo> findAllByWarehouseId(String warehouseId);

}
