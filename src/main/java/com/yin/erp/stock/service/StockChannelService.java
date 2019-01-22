package com.yin.erp.stock.service;

import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.stock.dao.StockChannelDao;
import com.yin.erp.stock.entity.bo.StockBo;
import com.yin.erp.stock.entity.po.StockChannelPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * 库存渠道服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class StockChannelService {

    @Autowired
    private StockChannelDao stockChannelDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsColorDao goodsColorDao;
    @Autowired
    private DictSizeDao dictSizeDao;

    /**
     * 增加库存
     *
     * @throws MessageException
     */
    public void add(BillDetailPo billDetailPo, String channelId) throws MessageException {
        this.add(new StockBo(channelId, null, billDetailPo.getGoodsId(), billDetailPo.getGoodsColorId(), billDetailPo.getGoodsSizeId(), billDetailPo.getBillCount()));
    }

    /**
     * 增加减少
     *
     * @throws MessageException
     */
    public void minus(BillDetailPo billDetailPo, String channelId) throws MessageException {
        this.minus(new StockBo(channelId, null, billDetailPo.getGoodsId(), billDetailPo.getGoodsColorId(), billDetailPo.getGoodsSizeId(), billDetailPo.getBillCount()));
    }

    /**
     * 增加库存
     *
     * @param stockBo
     * @throws Exception
     */
    public void add(@Validated StockBo stockBo) throws MessageException {
        if (StringUtils.isBlank(stockBo.getChannelId())) {
            throw new MessageException("渠道不能为空");
        }
        StockChannelPo stockChannelPo = this.getStockChannelPo(stockBo);
        stockChannelPo.setStockCount(stockChannelPo.getStockCount() + stockBo.getStockCount());
        //TODO 负库存
        stockChannelDao.save(stockChannelPo);
    }

    /**
     * 增加减少
     *
     * @param stockBo
     * @return
     */
    public void minus(@Validated StockBo stockBo) throws MessageException {
        if (StringUtils.isBlank(stockBo.getChannelId())) {
            throw new MessageException("渠道不能为空");
        }
        StockChannelPo stockChannelPo = this.getStockChannelPo(stockBo);
        stockChannelPo.setStockCount(stockChannelPo.getStockCount() - stockBo.getStockCount());
        //TODO 负库存
        stockChannelDao.save(stockChannelPo);
    }

    /**
     * 获取库存对象
     *
     * @param stockBo
     * @return
     * @throws MessageException
     */
    private StockChannelPo getStockChannelPo(StockBo stockBo) throws MessageException {
        StockChannelPo stockChannelPo = stockChannelDao.findByChannelIdAndGoodsIdAndGoodsColorIdAndGoodsSizeId(stockBo.getChannelId(), stockBo.getGoodsId(), stockBo.getGoodsColorId(), stockBo.getGoodsSizeId());
        if (stockChannelPo == null) {
            stockChannelPo = new StockChannelPo();
            ChannelPo channelPo = channelDao.findById(stockBo.getChannelId()).get();
            stockChannelPo.setChannelId(channelPo.getId());
            stockChannelPo.setChannelCode(channelPo.getCode());
            stockChannelPo.setChannelName(channelPo.getName());
            GoodsPo goodsPo = goodsDao.findById(stockBo.getGoodsId()).get();
            stockChannelPo.setGoodsId(goodsPo.getId());
            stockChannelPo.setGoodsCode(goodsPo.getCode());
            stockChannelPo.setGoodsName(goodsPo.getName());
            GoodsColorPo goodsColorPo = goodsColorDao.findByGoodsIdAndColorId(goodsPo.getId(), stockBo.getGoodsColorId());
            stockChannelPo.setGoodsColorId(goodsColorPo.getColorId());
            stockChannelPo.setGoodsColorCode(goodsColorPo.getColorCode());
            stockChannelPo.setGoodsColorName(goodsColorPo.getColorName());
            DictSizePo dictSizePo = dictSizeDao.findById(stockBo.getGoodsSizeId()).get();
            if (!dictSizePo.getGroupId().equals(goodsPo.getSizeGroupId())) {
                throw new MessageException("尺码不存在");
            }
            stockChannelPo.setGoodsSizeId(dictSizePo.getId());
            stockChannelPo.setGoodsSizeName(dictSizePo.getName());
        }
        return stockChannelPo;
    }

    /**
     * 查询库存
     *
     * @param channelId
     * @param goodsId
     * @param colorId
     * @param sizeId
     * @return
     */
    public Integer stockCount(String channelId, String goodsId, String colorId, String sizeId) {
        return stockChannelDao.findByChannelIdAndGoodsIdAndGoodsColorIdAndGoodsSizeId(channelId, goodsId, colorId, sizeId).getStockCount();
    }

}
