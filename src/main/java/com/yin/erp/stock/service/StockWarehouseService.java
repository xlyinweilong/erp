package com.yin.erp.stock.service;

import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.dao.GoodsInSizeDao;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import com.yin.erp.info.goods.entity.po.GoodsInSizePo;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.warehouse.dao.WarehouseDao;
import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import com.yin.erp.stock.dao.StockWarehouseDao;
import com.yin.erp.stock.entity.bo.StockBo;
import com.yin.erp.stock.entity.po.StockWarehousePo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 库存仓库服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class StockWarehouseService {

    @Autowired
    private StockWarehouseDao stockWarehouseDao;
    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsColorDao goodsColorDao;
    @Autowired
    private GoodsInSizeDao goodsInSizeDao;
    @Autowired
    private DictSizeDao dictSizeDao;

    /**
     * 增加库存
     *
     * @throws MessageException
     */
    public void add(BillDetailPo billDetailPo, String warehouseId) throws MessageException {
        this.add(new StockBo(null, warehouseId, billDetailPo.getGoodsId(), billDetailPo.getGoodsColorId(), billDetailPo.getGoodsInSizeId(), billDetailPo.getGoodsSizeId(), billDetailPo.getBillCount()));
    }

    /**
     * 增加减少
     *
     * @throws MessageException
     */
    public void minus(BillDetailPo billDetailPo, String warehouseId) throws MessageException {
        this.minus(new StockBo(null, warehouseId, billDetailPo.getGoodsId(), billDetailPo.getGoodsColorId(), billDetailPo.getGoodsInSizeId(), billDetailPo.getGoodsSizeId(), billDetailPo.getBillCount()));
    }

    /**
     * 增加库存
     *
     * @param stockBo
     * @throws Exception
     */
    private void add(@Validated StockBo stockBo) throws MessageException {
        if (StringUtils.isBlank(stockBo.getWarehouseId())) {
            throw new MessageException("仓库不能为空");
        }
        StockWarehousePo stockWarehousePo = this.getStockWarehousePo(stockBo);
        stockWarehousePo.setStockCount(stockWarehousePo.getStockCount() + stockBo.getStockCount());
        //TODO 负库存
        stockWarehouseDao.save(stockWarehousePo);
    }

    /**
     * 增加减少
     *
     * @param stockBo
     * @return
     */
    private void minus(@Validated StockBo stockBo) throws MessageException {
        if (StringUtils.isBlank(stockBo.getWarehouseId())) {
            throw new MessageException("仓库不能为空");
        }
        StockWarehousePo stockWarehousePo = this.getStockWarehousePo(stockBo);
        stockWarehousePo.setStockCount(stockWarehousePo.getStockCount() - stockBo.getStockCount());
        //TODO 负库存
        stockWarehouseDao.save(stockWarehousePo);
    }

    /**
     * 获取库存对象
     *
     * @param stockBo
     * @return
     * @throws MessageException
     */
    private StockWarehousePo getStockWarehousePo(StockBo stockBo) throws MessageException {
        StockWarehousePo stockWarehousePo = stockWarehouseDao.findByWarehouseIdAndGoodsIdAndGoodsColorIdAndGoodsInSizeIdAndGoodsSizeId(stockBo.getWarehouseId(), stockBo.getGoodsId(), stockBo.getGoodsColorId(), stockBo.getGoodsInSizeId(), stockBo.getGoodsSizeId());
        if (stockWarehousePo == null) {
            stockWarehousePo = new StockWarehousePo();
            WarehousePo warehousePo = warehouseDao.findById(stockBo.getWarehouseId()).get();
            stockWarehousePo.setWarehouseId(warehousePo.getId());
            stockWarehousePo.setWarehouseCode(warehousePo.getCode());
            stockWarehousePo.setWarehouseName(warehousePo.getName());
            GoodsPo goodsPo = goodsDao.findById(stockBo.getGoodsId()).get();
            stockWarehousePo.setGoodsId(goodsPo.getId());
            stockWarehousePo.setGoodsCode(goodsPo.getCode());
            stockWarehousePo.setGoodsName(goodsPo.getName());
            GoodsColorPo goodsColorPo = goodsColorDao.findByGoodsIdAndColorId(goodsPo.getId(), stockBo.getGoodsColorId());
            stockWarehousePo.setGoodsColorId(goodsColorPo.getColorId());
            stockWarehousePo.setGoodsColorCode(goodsColorPo.getColorCode());
            stockWarehousePo.setGoodsColorName(goodsColorPo.getColorName());
            GoodsInSizePo goodsInSizePo = goodsInSizeDao.findByGoodsIdAndInSizeId(goodsPo.getId(), stockBo.getGoodsInSizeId());
            stockWarehousePo.setGoodsInSizeId(goodsInSizePo.getInSizeId());
            stockWarehousePo.setGoodsInSizeName(goodsInSizePo.getInSizeName());
            DictSizePo dictSizePo = dictSizeDao.findById(stockBo.getGoodsSizeId()).get();
            if (!dictSizePo.getGroupId().equals(goodsPo.getSizeGroupId())) {
                throw new MessageException("尺码不存在");
            }
            stockWarehousePo.setGoodsSizeId(dictSizePo.getId());
            stockWarehousePo.setGoodsSizeName(dictSizePo.getName());
        }
        return stockWarehousePo;
    }


}
