package com.yin.erp.pos.back.service;

import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.exceptions.MessageException;
import com.yin.common.utils.GenerateUtil;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.pos.back.entity.vo.in.BackGoodsVo;
import com.yin.erp.pos.back.entity.vo.in.BackPayVo;
import com.yin.erp.pos.back.entity.vo.in.BackPaymentVo;
import com.yin.erp.pos.back.entity.vo.in.BackSearchVo;
import com.yin.erp.pos.back.entity.vo.out.BackVo;
import com.yin.erp.pos.cash.dao.PosCashDao;
import com.yin.erp.pos.cash.dao.PosCashDetailDao;
import com.yin.erp.pos.cash.dao.PosCashPaymentDao;
import com.yin.erp.pos.cash.entity.po.PosCashDetailPo;
import com.yin.erp.pos.cash.entity.po.PosCashPaymentPo;
import com.yin.erp.pos.cash.entity.po.PosCashPo;
import com.yin.erp.stock.entity.bo.StockBo;
import com.yin.erp.stock.service.StockChannelService;
import com.yin.erp.vip.info.dao.VipDao;
import com.yin.erp.vip.info.entity.po.VipPo;
import com.yin.erp.vip.info.service.VipService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 销售服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class BackService {

    @Autowired
    private PosCashDao posCashDao;
    @Autowired
    private PosCashDetailDao posCashDetailDao;
    @Autowired
    private PosCashPaymentDao posCashPaymentDao;
    @Autowired
    private VipDao vipDao;
    @Autowired
    private VipService vipService;
    @Autowired
    private StockChannelService stockChannelService;


    /**
     * 退货
     *
     * @param vo
     * @throws MessageException
     */
    public PosCashPo doBack(BackPayVo vo, UserSessionBo userSessionBo) throws MessageException {
        Date now = new Date();
        PosCashPo posCashPo = posCashDao.findByCode(vo.getBillCode());
        if (posCashPo == null) {
            throw new MessageException("单号不存在");
        }
        PosCashPo backPo = new PosCashPo();
        backPo.setParentId(posCashPo.getId());
        backPo.setCode("POSB" + GenerateUtil.createSerialNumber());
        backPo.setCreateUserName(userSessionBo.getName());
        backPo.setCreateUserId(userSessionBo.getId());
        backPo.setBillDate(now);
        backPo.setVipId(posCashPo.getVipId());
        backPo.setVipName(posCashPo.getVipName());
        backPo.setVipCode(posCashPo.getVipCode());
        backPo.setStatus(BillStatusEnum.AUDITED.name());
        backPo.setChannelCode(posCashPo.getChannelCode());
        backPo.setChannelName(posCashPo.getChannelName());
        backPo.setChannelId(posCashPo.getChannelId());
        backPo.setAuditUserName(userSessionBo.getName());
        backPo.setAuditUserId(userSessionBo.getId());
        backPo.setAuditDate(now);
        VipPo vip = null;
        if (StringUtils.isNotBlank(backPo.getVipId())) {
            vip = vipDao.findById(backPo.getVipId()).get();
        }
        for (BackGoodsVo backGoodsVo : vo.getGoodsList()) {
            PosCashDetailPo posCashDetailPo = posCashDetailDao.findById(backGoodsVo.getId()).get();
            posCashDetailPo.setBackCount(backGoodsVo.getBackCount());
            posCashDetailDao.save(posCashDetailPo);
            PosCashDetailPo backDetail = new PosCashDetailPo();
            CopyUtil.copyProperties(backDetail, backGoodsVo);
            backDetail.setId(GenerateUtil.createUUID());
            backDetail.setBackCount(0);
            backDetail.setBillId(backPo.getId());
            backDetail.setBillDate(now);
            backDetail.setBillCount(-backGoodsVo.getBackCount());
            backDetail.setAmount(backGoodsVo.getPrice().multiply(BigDecimal.valueOf(-backGoodsVo.getBackCount())));
            posCashDetailDao.save(backDetail);
            backPo.setTotalTagAmount(backPo.getTotalTagAmount().add(backDetail.getTagPrice().multiply(BigDecimal.valueOf(backGoodsVo.getBackCount()))));
            backPo.setTotalCount(backPo.getTotalCount() + backDetail.getBillCount());
            backPo.setTotalAmount(backPo.getTotalAmount().add(backDetail.getAmount()));
            if (vip != null) {
                for (int i = 0; i < backGoodsVo.getBackCount(); i++) {
                    if (backGoodsVo.getIntegral() > 0) {
                        vipService.addIntegralLog(vip.getId(), vip.getCode(), backGoodsVo.getIntegral(), "退货减少积分：" + posCashPo.getCode() + "；货号：" + backGoodsVo.getGoodsCode());
                    }
                    if (backGoodsVo.getXp() > 0) {
                        vipService.addXpLog(vip.getId(), vip.getCode(), backGoodsVo.getXp(), "退货减少经验：" + posCashPo.getCode() + "；货号：" + backGoodsVo.getGoodsCode());
                    }

                }
            }
            //库存退回
            stockChannelService.add(new StockBo(backPo.getChannelId(), null, backDetail.getGoodsId(), backDetail.getGoodsColorId(), backDetail.getGoodsSizeId(), backGoodsVo.getBackCount()));
        }
        //查询原来的支付方式
//        List<PosCashPaymentPo> oldPaymentList = posCashPaymentDao.findAllByBillId(posCashPo.getId());
//        BigDecimal totalAmount = oldPaymentList.stream().map(PosCashPaymentPo::getAmount).reduce((a, b) -> a.add(b)).get();
//        BigDecimal totalBackAmount = vo.getGoodsList().stream().map(g -> g.getPrice().multiply(BigDecimal.valueOf(-g.getBackCount()))).reduce((a, b) -> a.add(b)).get();
        //根据原来的支付比例，返还金额
        for (PosCashPaymentPo payment : vo.getPaymentList()) {
            PosCashPaymentPo posCashPaymentPo = new PosCashPaymentPo();
            CopyUtil.copyProperties(posCashPaymentPo, payment);
            posCashPaymentPo.setBillDate(now);
            posCashPaymentPo.setBillId(backPo.getId());
            posCashPaymentPo.setId(GenerateUtil.createUUID());
            posCashPaymentPo.setAmount(payment.getAmount().negate());
            posCashPaymentDao.save(posCashPaymentPo);
            //退回获得的积分
            if ("INTEGRAL".equals(posCashPaymentPo.getSysType()) && vip != null) {
                vip.setIntegral(vip.getIntegral() + posCashPaymentPo.getIntegral());
            }
            //退回获得的余额
            if ("BALANCE".equals(posCashPaymentPo.getSysType()) && vip != null) {
                vip.setBalance(vip.getBalance().add(posCashPaymentPo.getAmount()));
            }
        }
        //退回积分 退回经验 退回余额 增加日志
        if (vip != null) {
            //退回获得的积分
            vip.setIntegral(vip.getIntegral() - vo.getGoodsList().stream().map(g -> g.getIntegral() * g.getBackCount()).reduce((a, b) -> a + b).get());
            //退回获得经验
            vip.setXpValue(vip.getXpValue() - vo.getGoodsList().stream().map(g -> g.getXp() * g.getBackCount()).reduce((a, b) -> a + b).get());
            vipDao.save(vip);
        }
        posCashDao.save(backPo);
        return backPo;
    }

    /**
     * 查询要返回的支付方式和金额
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    public BackPaymentVo backPayment(BackPayVo vo) throws MessageException {
        Date now = new Date();
        PosCashPo posCashPo = posCashDao.findByCode(vo.getBillCode());
        if (posCashPo == null) {
            throw new MessageException("单号不存在");
        }
        //查询原来的支付方式
        List<PosCashPaymentPo> oldPaymentList = posCashPaymentDao.findAllByBillId(posCashPo.getId());
        BigDecimal totalBackAmount = vo.getGoodsList().stream().map(g -> g.getPrice().multiply(BigDecimal.valueOf(g.getBackCount()))).reduce((a, b) -> a.add(b)).get();
        //根据原来的支付比例，返还金额
        BackPaymentVo backPaymentVo = new BackPaymentVo();
        backPaymentVo.setPaymentList(oldPaymentList);
        backPaymentVo.setTotalBackAmount(totalBackAmount);
        if (StringUtils.isNotBlank(posCashPo.getVipId())) {
            backPaymentVo.setVip(vipDao.findById(posCashPo.getVipId()).get());
        }
        return backPaymentVo;
    }


    /**
     * 查询货品
     *
     * @param vo
     */
    public BackVo findGoodsList(BackSearchVo vo) throws MessageException {
        PosCashPo posCashPo = posCashDao.findByCode(vo.getBillCode());
        if (posCashPo == null) {
            throw new MessageException("单号不存在");
        }
        if (!posCashPo.getChannelId().equals(vo.getChannelId())) {
            throw new MessageException("单号不是当前渠道");
        }
        BackVo backVo = new BackVo();
        backVo.setPosCashPo(posCashPo);
        List<PosCashDetailPo> posCashDetailPoList = posCashDetailDao.findBackByBillId(posCashPo.getId());
        backVo.setGoodsList(posCashDetailPoList);
        this.findAllDetail(posCashPo, posCashDetailPoList, backVo);
        return backVo;
    }

    /**
     * 查询所有货品
     *
     * @param posCashPo
     * @param detailPoList
     */
    private void findAllDetail(PosCashPo posCashPo, List<PosCashDetailPo> detailPoList, BackVo backVo) {
        if (StringUtils.isNotBlank(posCashPo.getParentId())) {
            PosCashPo po = posCashDao.findById(posCashPo.getParentId()).get();
            backVo.setPosCashPo(po);
            List<PosCashDetailPo> list = posCashDetailDao.findBackByBillId(posCashPo.getId());
            detailPoList.addAll(list);
            backVo.setGoodsList(detailPoList);
            this.findAllDetail(po, detailPoList, backVo);
        }
    }


}
