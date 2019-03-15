package com.yin.erp.bill.common.service;

import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.exceptions.MessageException;
import com.yin.common.utils.GenerateUtil;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.bill.common.dao.BaseBillDao;
import com.yin.erp.bill.common.dao.BaseBillDetailDao;
import com.yin.erp.bill.common.dao.BaseBillGoodsDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.po.BillQuotedPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BillDetailVo;
import com.yin.erp.bill.common.entity.vo.in.BillGoodsVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.po.DictPo;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.supplier.dao.SupplierDao;
import com.yin.erp.info.supplier.entity.po.SupplierPo;
import com.yin.erp.info.warehouse.dao.WarehouseDao;
import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 单据服务
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class BillCommonSaveService {

    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private DictDao dictDao;
    @Autowired
    private DictSizeDao dictSizeDao;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;


    /**
     * 保存对象复用方法
     *
     * @param dbPo
     * @param vo
     * @param userSessionBo
     * @param billDao
     * @param billGoodsDao
     * @param billDetailDao
     * @param billPrefixKey
     * @param parentBillGoods
     * @throws MessageException
     */
    public BillPo save(BillPo dbPo, BillVo vo, UserSessionBo userSessionBo, BaseBillDao billDao, BaseBillGoodsDao billGoodsDao, BaseBillDetailDao billDetailDao, String billPrefixKey, List<BillGoodsVo> parentBillGoods, BaseBillDao parentBillDao) throws MessageException {
        CopyUtil.copyProperties(dbPo, vo);
        dbPo.setParentBillCode(StringUtils.trimToNull(dbPo.getParentBillCode()));
        dbPo.setParentBillId(StringUtils.trimToNull(dbPo.getParentBillId()));
        dbPo.setGrandParentBillCode(StringUtils.trimToNull(dbPo.getGrandParentBillCode()));
        dbPo.setGrandParentBillId(StringUtils.trimToNull(dbPo.getGrandParentBillId()));
        if (StringUtils.isBlank(dbPo.getId())) {
            dbPo.setId(GenerateUtil.createUUID());
            dbPo.setCode(billPrefixKey + GenerateUtil.createSerialNumber());
            dbPo.setCreateUserId(userSessionBo.getId());
            dbPo.setCreateUserName(userSessionBo.getName());
            if (dbPo instanceof BillQuotedPo) {
                BillQuotedPo qpo = (BillQuotedPo) dbPo;
                qpo.setTotalQuotedCount(0);
            }
            //判断上游状态
            if (StringUtils.isNotBlank(vo.getParentBillId())) {
                BillPo parentPo = (BillPo) parentBillDao.findById(vo.getParentBillId()).get();
                if (!parentPo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                    throw new MessageException("上游单据状态错误，请退回到列表刷新后重试");
                }
            }
        } else {
            //判断状态
            BillPo oldPo = (BillPo) billDao.findById(dbPo.getId()).get();
            if (oldPo.getStatus().equals(BillStatusEnum.AUDITED.name()) || oldPo.getStatus().equals(BillStatusEnum.COMPLETE.name()) || oldPo.getStatus().equals(BillStatusEnum.QUOTE.name())) {
                throw new MessageException("单据状态错误，请退回到列表刷新后重试");
            }
            //判断上游状态
            if (StringUtils.isNotBlank(vo.getParentBillId())) {
                BillPo parentPo = (BillPo) parentBillDao.findById(vo.getParentBillId()).get();
                if (!parentPo.getStatus().equals(BillStatusEnum.AUDITED.name()) && !vo.getParentBillId().equals(oldPo.getParentBillId())) {
                    throw new MessageException("上游单据状态错误，请退回到列表刷新后重试");
                }
            }
            dbPo.setVersion(oldPo.getVersion());
            //删除
            billDetailDao.deleteAllByBillId(dbPo.getId());
            billGoodsDao.deleteAllByBillId(dbPo.getId());
        }
        dbPo.setManualCode(StringUtils.trimToNull(vo.getManualCode()));
        dbPo.setTotalCount(0);
        dbPo.setTotalAmount(BigDecimal.ZERO);
        dbPo.setTotalTagAmount(BigDecimal.ZERO);
        if (StringUtils.isNotBlank(vo.getChannelId())) {
            ChannelPo channelPo = channelDao.findById(vo.getChannelId()).get();
            dbPo.setChannelCode(channelPo.getCode());
            dbPo.setChannelName(channelPo.getName());
        }
        if (StringUtils.isNotBlank(vo.getToChannelId())) {
            if(vo.getToChannelId().equals(vo.getChannelId())){
                throw new MessageException("调出渠道和调入渠道相同");
            }
            ChannelPo channelPo = channelDao.findById(vo.getToChannelId()).get();
            dbPo.setToChannelCode(channelPo.getCode());
            dbPo.setToChannelName(channelPo.getName());
        }
        if (StringUtils.isNotBlank(vo.getSupplierId())) {
            SupplierPo supplierPo = supplierDao.findById(vo.getSupplierId()).get();
            dbPo.setSupplierName(supplierPo.getName());
            dbPo.setSupplierCode(supplierPo.getCode());
        }
        if (StringUtils.isNotBlank(vo.getWarehouseId())) {
            WarehousePo warehousePo = warehouseDao.findById(vo.getWarehouseId()).get();
            dbPo.setWarehouseName(warehousePo.getName());
            dbPo.setWarehouseCode(warehousePo.getCode());
        }
        List<BillGoodsPo> goodsList = new ArrayList<>();
        List<BillDetailPo> detailList = new ArrayList<>();
        int billOrder = 0;
        for (BillGoodsVo goodsVo : vo.getGoodsList()) {
            GoodsPo goodsPo = goodsDao.findById(goodsVo.getGoodsId()).get();
            if (parentBillGoods != null) {
                Optional<BillGoodsVo> dbGoods = parentBillGoods.stream().filter(g -> g.getGoodsId().equals(goodsVo.getGoodsId())).findFirst();
//                if (!dbGoods.isPresent()) {
//                    throw new MessageException("上游单据没有货号：" + goodsPo.getCode());
//                }
            }
            BillGoodsPo billGoodsPo = dbPo.getBillGoodsInstance();
            billGoodsPo.setBillDate(vo.getBillDate());
            billGoodsPo.setId(GenerateUtil.createUUID());
            billGoodsPo.setGoodsCode(goodsPo.getCode());
            billGoodsPo.setGoodsId(goodsPo.getId());
            billGoodsPo.setGoodsName(goodsPo.getName());
            billGoodsPo.setPrice(goodsVo.getPrice());
            billGoodsPo.setBillId(dbPo.getId());
            billGoodsPo.setTagPrice(goodsVo.getTagPrice());
            billGoodsPo.setBillOrder(billOrder++);
            for (BillDetailVo billDetailVo : goodsVo.getDetail()) {
                DictPo colorPo = dictDao.findById(billDetailVo.getColorId()).get();
                DictSizePo sizePo = dictSizeDao.findById(billDetailVo.getSizeId()).get();
                if (parentBillGoods != null) {
                    Optional<BillGoodsVo> dbGoods = parentBillGoods.stream().filter(g -> g.getGoodsId().equals(goodsVo.getGoodsId())).findFirst();
                    if(dbGoods.isPresent()){
                        Optional<BillDetailVo> dbDetail = dbGoods.get().getDetail().stream().filter(d -> d.getSizeId().equals(billDetailVo.getSizeId()) && d.getColorId().equals(billDetailVo.getColorId())).findFirst();
                        if (!dbDetail.isPresent()) {
//                        throw new MessageException("上游单据没有货号：" + goodsVo.getGoodsCode() + ",颜色编号：" + colorPo.getCode() + ",颜色名称：" + colorPo.getName() + ",尺码：" + sizePo.getName());
                        } else if (dbDetail.get().getBillCount() < billDetailVo.getBillCount()) {
//                        throw new MessageException("上游单据里货号：" + goodsVo.getGoodsCode() + ",颜色编号：" + colorPo.getCode() + ",颜色名称：" + colorPo.getName() + ",尺码：" + sizePo.getName()
//                                + "的数量不足，只有：" + dbDetail.get().getBillCount());
                        }
                    }else{
                        //throw new MessageException("上游单据没有货号：" + goodsVo.getGoodsCode());
                    }
                }
                BillDetailPo billDetailPo = dbPo.getBillDetailInstance();
                billDetailPo.setBillCount(billDetailVo.getBillCount());
                billDetailPo.setBillDate(dbPo.getBillDate());
                billDetailPo.setBillGoodsId(billGoodsPo.getId());
                billDetailPo.setBillId(dbPo.getId());
                billDetailPo.setGoodsCode(goodsPo.getCode());
                billDetailPo.setGoodsId(goodsPo.getId());
                billDetailPo.setGoodsName(goodsPo.getName());
                billDetailPo.setGoodsColorCode(colorPo.getCode());
                billDetailPo.setGoodsColorId(colorPo.getId());
                billDetailPo.setGoodsColorName(colorPo.getName());
                billDetailPo.setGoodsSizeId(sizePo.getId());
                billDetailPo.setGoodsSizeName(sizePo.getName());
                billDetailPo.setPrice(billGoodsPo.getPrice());
                billDetailPo.setTagPrice(billGoodsPo.getTagPrice());
                detailList.add(billDetailPo);
                billDetailDao.saveAndFlush(billDetailPo);
                //求和
                billGoodsPo.setBillCount(billGoodsPo.getBillCount() + billDetailVo.getBillCount());
            }
            goodsList.add(billGoodsPo);
            //求和
            dbPo.setTotalCount(dbPo.getTotalCount() + billGoodsPo.getBillCount());
            dbPo.setTotalAmount(dbPo.getTotalAmount().add(billGoodsPo.getPrice().multiply(BigDecimal.valueOf(billGoodsPo.getBillCount()))));
            dbPo.setTotalTagAmount(dbPo.getTotalTagAmount().add(billGoodsPo.getTagPrice().multiply(BigDecimal.valueOf(billGoodsPo.getBillCount()))));
        }
        billDetailDao.saveAll(detailList);
        billGoodsDao.saveAll(goodsList);
        billDao.saveAndFlush(dbPo);
        return dbPo;
    }

}
