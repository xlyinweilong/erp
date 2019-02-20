package com.yin.erp.stock.service;

import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.config.sysconfig.service.ConfigService;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
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
    private DictSizeDao dictSizeDao;
    @Autowired
    private DictDao dictDao;
    @Autowired
    private ConfigService configService;

    /**
     * 增加库存
     *
     * @throws MessageException
     */
    public void add(BillDetailPo billDetailPo, String warehouseId) throws MessageException {
        this.add(new StockBo(null, warehouseId, billDetailPo.getGoodsId(), billDetailPo.getGoodsColorId(), billDetailPo.getGoodsSizeId(), billDetailPo.getBillCount()));
    }

    /**
     * 增加减少
     *
     * @throws MessageException
     */
    public void minus(BillDetailPo billDetailPo, String warehouseId) throws MessageException {
        this.minus(new StockBo(null, warehouseId, billDetailPo.getGoodsId(), billDetailPo.getGoodsColorId(), billDetailPo.getGoodsSizeId(), billDetailPo.getBillCount()));
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
        if (stockWarehousePo.getStockCount() < 0 && stockBo.getStockCount() < 0 && configService.getSysConfigValue("system_warehouse_stock_bufu") == 0) {
            //负库存
            //查询货号、颜色、尺码
            throw new MessageException("库存不能为负数,货号：" + goodsDao.findById(stockBo.getGoodsId()).get().getCode() + ",颜色：" + dictDao.findById(stockBo.getGoodsColorId()).get().getName() + ",尺码：" + dictSizeDao.findById(stockBo.getGoodsSizeId()).get().getName());
        }
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
        if (stockWarehousePo.getStockCount() < 0 && stockBo.getStockCount() > 0 && configService.getSysConfigValue("system_warehouse_stock_bufu") == 0) {
            //负库存
            //查询货号、颜色、尺码
            throw new MessageException("库存不能为负数,货号：" + goodsDao.findById(stockBo.getGoodsId()).get().getCode() + ",颜色：" + dictDao.findById(stockBo.getGoodsColorId()).get().getName() + ",尺码：" + dictSizeDao.findById(stockBo.getGoodsSizeId()).get().getName());
        }
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
        StockWarehousePo stockWarehousePo = stockWarehouseDao.findByWarehouseIdAndGoodsIdAndGoodsColorIdAndGoodsSizeId(stockBo.getWarehouseId(), stockBo.getGoodsId(), stockBo.getGoodsColorId(), stockBo.getGoodsSizeId());
        if (stockWarehousePo == null) {
            stockWarehousePo = new StockWarehousePo();
            WarehousePo warehousePo = warehouseDao.findById(stockBo.getWarehouseId()).get();
            stockWarehousePo.setWarehouseGroupId(warehousePo.getGroupId());
            stockWarehousePo.setWarehouseId(warehousePo.getId());
            stockWarehousePo.setWarehouseCode(warehousePo.getCode());
            stockWarehousePo.setWarehouseName(warehousePo.getName());
            GoodsPo goodsPo = goodsDao.findById(stockBo.getGoodsId()).get();
            stockWarehousePo.setGoodsGroupId(goodsPo.getGoodsGroupId());
            stockWarehousePo.setGoodsId(goodsPo.getId());
            stockWarehousePo.setGoodsCode(goodsPo.getCode());
            stockWarehousePo.setGoodsName(goodsPo.getName());
            GoodsColorPo goodsColorPo = goodsColorDao.findByGoodsIdAndColorId(goodsPo.getId(), stockBo.getGoodsColorId());
            stockWarehousePo.setGoodsColorId(goodsColorPo.getColorId());
            stockWarehousePo.setGoodsColorCode(goodsColorPo.getColorCode());
            stockWarehousePo.setGoodsColorName(goodsColorPo.getColorName());
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
