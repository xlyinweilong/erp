package com.yin.erp.bill.common.service;

import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.bill.common.dao.BaseBillInventoryDao;
import com.yin.erp.bill.common.dao.BaseBillInventoryDetailDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BillDetailVo;
import com.yin.erp.bill.common.entity.vo.in.BillGoodsVo;
import com.yin.erp.bill.common.entity.vo.in.BillInventoryVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.settlement.entity.po.SettlementPo;
import com.yin.erp.bill.settlement.service.SettlementService;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.warehouse.dao.WarehouseDao;
import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import com.yin.erp.stock.dao.StockChannelDao;
import com.yin.erp.stock.dao.StockWarehouseDao;
import com.yin.erp.stock.entity.po.BaseStockPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 盘点服务
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class BillCommonInventoryService {

    @Autowired
    private StockChannelDao stockChannelDao;
    @Autowired
    private StockWarehouseDao stockWarehouseDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private GoodsDao goodsDao;


    /**
     * 盘点
     *
     * @param vo
     * @param userSessionBo
     * @throws MessageException
     */
    public void inventory(String type, BillInventoryVo vo, UserSessionBo userSessionBo, BaseBillInventoryDao baseBillInventoryDao, BaseBillInventoryDetailDao baseBillInventoryDetailDao, BillService lossBillService) throws MessageException {
        //查出所有的盘点单据
        List<BillPo> inventoryPoList = baseBillInventoryDao.findAll4Pd(vo.getEleId(), vo.getDate());
        //查出盘点单据详情
        List<BillDetailPo> detailList = baseBillInventoryDetailDao.findInBillIds(inventoryPoList.stream().map(b -> b.getId()).collect(Collectors.toList()));
        //合并数量并生成损益
        List<BillGoodsVo> billGoodsVoList = new ArrayList<>();
        //生成一个结算
        SettlementPo settlement = null;
        ChannelPo channelPo = null;
        WarehousePo warehousePo = null;
        if (type.equals("WAREHOUSE")) {
            warehousePo = warehouseDao.findById(vo.getEleId()).get();
            settlement = settlementService.save(warehousePo, vo.getDate());
        } else {
            channelPo = channelDao.findById(vo.getEleId()).get();
            settlement = settlementService.save(channelPo, vo.getDate());
        }
        //修改盘点单据
        for (BillPo po : inventoryPoList) {
            po.setParentBillCode(settlement.getCode());
            po.setParentBillId(settlement.getId());
            po.setStatus(BillStatusEnum.COMPLETE.name());
            baseBillInventoryDao.save(po);
        }
        //损益单VO
        BillVo lossVo = new BillVo();
        lossVo.setStatus(BillStatusEnum.PENDING.name());
        lossVo.setBillDate(vo.getDate());
        lossVo.setParentBillCode(settlement.getCode());
        lossVo.setParentBillId(settlement.getId());
        if (type.equals("WAREHOUSE")) {
            lossVo.setWarehouseName(warehousePo.getName());
            lossVo.setWarehouseId(warehousePo.getId());
            lossVo.setWarehouseCode(warehousePo.getCode());
        } else {
            lossVo.setChannelName(channelPo.getName());
            lossVo.setChannelId(channelPo.getId());
            lossVo.setChannelCode(channelPo.getCode());
        }
        lossVo.setGoodsList(billGoodsVoList);
        //查询全部库存
        List<BaseStockPo> stockList = null;
        if (type.equals("WAREHOUSE")) {
            stockList = stockWarehouseDao.findAllByWarehouseId(vo.getEleId());
        } else {
            stockList = stockChannelDao.findAllByChannelId(vo.getEleId());
        }
        for (BillDetailPo dp : detailList) {
            //盘次
            int times = inventoryPoList.stream().filter(p -> p.getId().equals(dp.getBillId())).findFirst().get().getTimes();
            Optional<BillGoodsVo> optional = billGoodsVoList.stream().filter(d -> d.getGoodsId().equals(dp.getGoodsId())).findFirst();
            if (optional.isPresent()) {
                BillGoodsVo billDetailPo = optional.get();
                if (billDetailPo.getTimes() < times) {
                    List<BillDetailVo> detail = new ArrayList<>();
                    billDetailPo.setDetail(detail);
                    CopyUtil.copyProperties(billDetailPo, dp);
                    BillDetailVo billDetailVo = new BillDetailVo(-dp.getBillCount(), dp.getGoodsColorId(), dp.getGoodsColorCode(), dp.getGoodsColorName(), dp.getGoodsSizeId(), dp.getGoodsSizeName());
                    detail.add(billDetailVo);
                    billDetailPo.setTimes(times);
                } else if (billDetailPo.getTimes() == times) {
                    Optional<BillDetailVo> billDetailOptional = billDetailPo.getDetail().stream().filter(d -> d.getSizeId().equals(dp.getGoodsId()) && d.getColorId().equals(dp.getGoodsColorId())).findFirst();
                    if (billDetailOptional.isPresent()) {
                        BillDetailVo billDetailVo = billDetailOptional.get();
                        billDetailVo.setBillCount(billDetailVo.getBillCount() - dp.getBillCount());
                    } else {
                        BillDetailVo billDetailVo = new BillDetailVo(-dp.getBillCount(), dp.getGoodsColorId(), dp.getGoodsColorCode(), dp.getGoodsColorName(), dp.getGoodsSizeId(), dp.getGoodsSizeName());
                        billDetailPo.getDetail().add(billDetailVo);
                    }
                } else if (billDetailPo.getTimes() > times) {
                    continue;
                }
            } else {
                List<BillDetailVo> detail = new ArrayList<>();
                BillGoodsVo billDetailPo = new BillGoodsVo();
                billDetailPo.setDetail(detail);
                CopyUtil.copyProperties(billDetailPo, dp);
                billDetailPo.setPrice(dp.getPrice());
                billDetailPo.setTagPrice(dp.getTagPrice());
                BillDetailVo billDetailVo = new BillDetailVo(-dp.getBillCount(), dp.getGoodsColorId(), dp.getGoodsColorCode(), dp.getGoodsColorName(), dp.getGoodsSizeId(), dp.getGoodsSizeName());
                detail.add(billDetailVo);
                billDetailPo.setTimes(times);
                billGoodsVoList.add(billDetailPo);
            }
        }
        if (vo.getType().equals("ALL")) {
            //整盘
            //计算损益
            for (BaseStockPo stockPo : stockList) {
                Optional<BillGoodsVo> optional = billGoodsVoList.stream().filter(d -> d.getGoodsId().equals(stockPo.getGoodsId())).findFirst();
                if (optional.isPresent()) {
                    BillGoodsVo billGoodsVo = optional.get();
                    Optional<BillDetailVo> billDetailOptional = billGoodsVo.getDetail().stream().filter(d -> d.getColorId().equals(stockPo.getGoodsColorId()) && d.getSizeId().equals(stockPo.getGoodsSizeId())).findFirst();
                    this.add(billDetailOptional, stockPo, billGoodsVo);
                } else {
                    List<BillDetailVo> detail = new ArrayList<>();
                    BillGoodsVo billDetailPo = new BillGoodsVo();
                    billDetailPo.setDetail(detail);
                    CopyUtil.copyProperties(billDetailPo, stockPo);
                    GoodsPo goodsPo = goodsDao.findById(stockPo.getGoodsId()).get();
                    billDetailPo.setPrice(goodsPo.getTagPrice1());
                    billDetailPo.setTagPrice(goodsPo.getTagPrice1());
                    BillDetailVo billDetailVo = new BillDetailVo(stockPo.getStockCount(), stockPo.getGoodsColorId(), stockPo.getGoodsColorCode(), stockPo.getGoodsColorName(), stockPo.getGoodsSizeId(), stockPo.getGoodsSizeName());
                    detail.add(billDetailVo);
                    billGoodsVoList.add(billDetailPo);
                }
            }
        } else if (vo.getType().equals("PART")) {
            //分盘
            for (BillGoodsVo billGoodsVo : billGoodsVoList) {
                List<BaseStockPo> stockPoList = stockList.stream().filter(s -> s.getGoodsId().equals(billGoodsVo.getGoodsId())).collect(Collectors.toList());
                if (stockPoList.size() > 0) {
                    for (BaseStockPo stockPo : stockPoList) {
                        Optional<BillDetailVo> billDetailOptional = billGoodsVo.getDetail().stream().filter(d -> d.getSizeId().equals(stockPo.getGoodsSizeId()) && d.getColorId().equals(stockPo.getGoodsColorId())).findFirst();
                        this.add(billDetailOptional, stockPo, billGoodsVo);
                    }
                } else {
                    continue;
                }
            }
        } else {
            throw new RuntimeException();
        }
        //生成损益单
        BillPo lossPo = lossBillService.save(lossVo, userSessionBo);
        //审核损益单
        BaseAuditVo baseAuditVo = new BaseAuditVo();
        baseAuditVo.setIds(Arrays.asList(lossPo.getId()));
        baseAuditVo.setStatus(BillStatusEnum.COMPLETE.name());
        lossBillService.audit(baseAuditVo, userSessionBo);
    }

    /**
     * 增加明细
     *
     * @param billDetailOptional
     * @param stockPo
     * @param billGoodsVo
     */
    private void add(Optional<BillDetailVo> billDetailOptional, BaseStockPo stockPo, BillGoodsVo billGoodsVo) {
        if (billDetailOptional.isPresent()) {
            BillDetailVo billDetailVo = billDetailOptional.get();
            billDetailVo.setBillCount(billDetailVo.getBillCount() + stockPo.getStockCount());
        } else {
            BillDetailVo billDetailVo = new BillDetailVo(stockPo.getStockCount(), stockPo.getGoodsColorId(), stockPo.getGoodsColorCode(), stockPo.getGoodsColorName(), stockPo.getGoodsSizeId(), stockPo.getGoodsSizeName());
            billGoodsVo.getDetail().add(billDetailVo);
        }
    }

}
