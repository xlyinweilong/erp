package com.yin.erp.pos.cash.service;

import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.exceptions.MessageException;
import com.yin.common.utils.GenerateUtil;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.employ.entity.vo.EmployVo;
import com.yin.erp.info.marketpoint.dao.MarketPointDao;
import com.yin.erp.info.marketpoint.entity.po.MarketPointPo;
import com.yin.erp.pos.cash.dao.*;
import com.yin.erp.pos.cash.entity.po.*;
import com.yin.erp.pos.cash.entity.vo.in.PayVo;
import com.yin.erp.pos.cash.entity.vo.in.PosCashGoods;
import com.yin.erp.pos.cash.entity.vo.in.PosCashPayment;
import com.yin.erp.stock.entity.bo.StockBo;
import com.yin.erp.stock.service.StockChannelService;
import com.yin.erp.vip.coupon.dao.VipCouponDao;
import com.yin.erp.vip.coupon.entity.po.VipCouponPo;
import com.yin.erp.vip.info.dao.VipDao;
import com.yin.erp.vip.info.entity.po.VipPo;
import com.yin.erp.vip.info.service.VipService;
import com.yin.erp.vip.integral.service.VipIntegralRuleService;
import com.yin.erp.vip.xp.service.VipXpRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class CashService {

    @Autowired
    private PosCashDao posCashDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private PosCashDetailDao posCashDetailDao;
    @Autowired
    private PosCashPaymentDao posCashPaymentDao;
    @Autowired
    private StockChannelService stockChannelService;
    @Autowired
    private VipIntegralRuleService vipIntegralRuleService;
    @Autowired
    private VipXpRuleService vipXpRuleService;
    @Autowired
    private MarketPointDao marketPointDao;
    @Autowired
    private VipService vipService;
    @Autowired
    private VipDao vipDao;
    @Autowired
    private VipCouponDao vipCouponDao;
    @Autowired
    private PosCashCouponDao posCashCouponDao;
    @Autowired
    private PosCashEmployDao posCashEmployDao;


    /**
     * 支付
     *
     * @param payVo
     * @param user
     */
    public PosCashPo pay(PayVo payVo, UserSessionBo user) throws MessageException {
        //保存数据
        PosCashPo po = this.savePo(payVo, user);
        //调整库存
        for (PosCashGoods goodsVo : payVo.getGoodsList()) {
            stockChannelService.minus(new StockBo(payVo.getChannelId(), null, goodsVo.getId(), goodsVo.getGoodsColorId(), goodsVo.getGoodsSizeId(), goodsVo.getBillCount()));
        }
        return po;
    }

    /**
     * 保存单据信息
     *
     * @param payVo
     * @param user
     */
    public PosCashPo savePo(PayVo payVo, UserSessionBo user) throws MessageException {
        Date now = new Date();
        //保存单据
        PosCashPo po = new PosCashPo();
        po.setCode("POS" + GenerateUtil.createSerialNumber());
        po.setBillDate(now);
        po.setAuditDate(now);
        po.setAuditUserId(user.getId());
        po.setAuditUserName(user.getName());
        ChannelPo channelPo = channelDao.findById(payVo.getChannelId()).get();
        po.setChannelId(channelPo.getId());
        po.setCreateUserId(user.getId());
        po.setCreateUserName(user.getName());
        po.setChannelName(channelPo.getName());
        po.setChannelCode(channelPo.getCode());
        po.setStatus(BillStatusEnum.AUDITED.name());
        VipPo vipPo = null;
        if (StringUtils.isNotBlank(payVo.getVipId())) {
            vipPo = vipDao.findById(payVo.getVipId()).get();
            po.setVipCode(vipPo.getCode());
            po.setVipId(vipPo.getId());
            po.setVipName(vipPo.getName());
        }
        po.setTotalAmount(payVo.getGoodsList().stream().map(g -> g.getAmount()).reduce((a, b) -> a.add(b)).get());
        po.setTotalCount(payVo.getGoodsList().stream().map(g -> g.getBillCount()).reduce((a, b) -> a + b).get());
        po.setTotalTagAmount(payVo.getGoodsList().stream().map(g -> g.getTagPrice()).reduce((a, b) -> a.add(b)).get());
        posCashDao.save(po);
        //保存详情
        Integer totalXp = 0;
        Integer totalIntegral = 0;
        if (!payVo.getCouponList().isEmpty()) {
            for (VipCouponPo vipCouponPo : payVo.getCouponList()) {
                VipCouponPo vipCouponPoData = vipCouponDao.findById(vipCouponPo.getId()).get();
                vipCouponPoData.setUsed(true);
                vipCouponDao.save(vipCouponPoData);
                //保存待用卷
                PosCashCouponPo posCashCouponPo = new PosCashCouponPo();
                posCashCouponPo.setBillDate(po.getBillDate());
                posCashCouponPo.setBillId(po.getId());
                posCashCouponPo.setCouponAmount(vipCouponPoData.getAmount());
                posCashCouponPo.setCouponId(vipCouponPoData.getId());
                posCashCouponDao.save(posCashCouponPo);
            }
        }

        for (EmployVo employVo : payVo.getEmployList()) {
            PosCashEmployPo posCashEmployPo = new PosCashEmployPo();
            posCashEmployPo.setBillDate(now);
            posCashEmployPo.setChannelId(payVo.getChannelId());
            posCashEmployPo.setEmployCode(employVo.getCode());
            posCashEmployPo.setEmployId(employVo.getId());
            posCashEmployPo.setEmployName(employVo.getName());
            posCashEmployPo.setRate(employVo.getRate());
            posCashEmployDao.save(posCashEmployPo);
        }

        for (PosCashGoods goodsVo : payVo.getGoodsList()) {
            PosCashDetailPo detailPo = new PosCashDetailPo();
            detailPo.setStatus(BillStatusEnum.AUDITED.name());
            detailPo.setRemarks(StringUtils.trimToNull(goodsVo.getRemarks()));
            detailPo.setDiyPrice(goodsVo.getDiyPrice());
            detailPo.setActivityId(StringUtils.trimToNull(goodsVo.getActivityId()));
            detailPo.setVipDiscount(goodsVo.getVipDiscount());
            detailPo.setAmount(goodsVo.getAmount());
            detailPo.setSalePrice(goodsVo.getSalePrice());
            detailPo.setPrice(goodsVo.getPrice());
            detailPo.setTagPrice(goodsVo.getTagPrice());
            detailPo.setBillCount(goodsVo.getBillCount());
            detailPo.setBillDate(now);
            detailPo.setBillId(po.getId());
            detailPo.setChannelId(po.getChannelId());
            detailPo.setGoodsColorCode(goodsVo.getGoodsColorCode());
            detailPo.setGoodsColorId(goodsVo.getGoodsColorId());
            detailPo.setGoodsColorName(goodsVo.getGoodsColorName());
            detailPo.setGoodsSizeId(goodsVo.getGoodsSizeId());
            detailPo.setGoodsSizeName(goodsVo.getGoodsSizeName());
            detailPo.setGoodsId(goodsVo.getId());
            detailPo.setGoodsName(goodsVo.getName());
            detailPo.setGoodsCode(goodsVo.getCode());
//            detailPo.setEmployCode(StringUtils.trimToNull(goodsVo.getEmployCode()));
//            detailPo.setEmployId(StringUtils.trimToNull(goodsVo.getEmployId()));
//            detailPo.setEmployName(StringUtils.trimToNull(goodsVo.getEmployCode()));
            if (po.getVipId() != null) {
                //获取积分
                Integer integralRule = vipIntegralRuleService.calculateIntegral(payVo.getVipGradeId(), now, detailPo.getGoodsId(), goodsVo.getGoodsBrandId(), goodsVo.getGoodsCategoryId(), goodsVo.getGoodsSeasonId(), goodsVo.getGoodsYearId());
                Integer integral = goodsVo.getPrice().multiply(BigDecimal.valueOf(integralRule)).intValue();
                detailPo.setIntegral(integral);
                totalIntegral += integral * goodsVo.getBillCount();
                //积分日志
                if (integral > 0) {
                    for (int i = 0; i < goodsVo.getBillCount(); i++) {
                        vipService.addIntegralLog(vipPo.getId(), vipPo.getCode(), integral, "购买增加积分：" + po.getCode() + "；货号：" + detailPo.getGoodsCode());
                    }
                }
                //获得经验
                Integer xpRule = vipXpRuleService.calculateXp(payVo.getVipGradeId(), now, detailPo.getGoodsId(), goodsVo.getGoodsBrandId(), goodsVo.getGoodsCategoryId(), goodsVo.getGoodsSeasonId(), goodsVo.getGoodsYearId());
                Integer xp = goodsVo.getPrice().multiply(BigDecimal.valueOf(xpRule)).intValue();
                detailPo.setXp(xp);
                totalXp += xp * goodsVo.getBillCount();
                //经验日志
                if (xp > 0) {
                    for (int i = 0; i < goodsVo.getBillCount(); i++) {
                        vipService.addXpLog(vipPo.getId(), vipPo.getCode(), xp, "购买增加经验：" + po.getCode() + "；货号：" + detailPo.getGoodsCode());
                    }
                }
            }
            //扣点
            String pointId = null;
            if (StringUtils.isNotBlank(goodsVo.getPointId())) {
                pointId = goodsVo.getPointId();
            } else {
                //获取渠道的扣点
                pointId = channelPo.getMarketPointId();
            }
            MarketPointPo marketPointPo = marketPointDao.findById(pointId).get();
            detailPo.setPointId(marketPointPo.getId());
            detailPo.setPoint(marketPointPo.getPoint());
            detailPo.setPointCode(marketPointPo.getCode());
            posCashDetailDao.save(detailPo);
        }
        boolean saveVipPo = false;
        //保存支付方式
        for (PosCashPayment paymentVo : payVo.getPaymentList()) {
            PosCashPaymentPo paymentPo = new PosCashPaymentPo();
            paymentPo.setBillDate(now);
            paymentPo.setAmount(paymentVo.getAmount());
            paymentPo.setBillId(po.getId());
            paymentPo.setIntegral(paymentVo.getIntegral());
            paymentPo.setPaymentId(paymentVo.getId());
            paymentPo.setPaymentName(paymentVo.getName());
            paymentPo.setSys(paymentVo.getSys());
            paymentPo.setSysType(paymentVo.getSysType());
            posCashPaymentDao.save(paymentPo);
            //使用余额
            if ("BALANCE".equals(paymentVo.getSysType())) {
                saveVipPo = true;
                vipPo.setBalance(vipPo.getBalance().subtract(paymentPo.getAmount()));
                vipService.addBalanceLog(vipPo.getId(), vipPo.getCode(), paymentPo.getAmount().negate(), "消费使用余额：" + po.getCode());
                if (vipPo.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                    throw new MessageException("余额不足");
                }
            }
            //使用积分
            if ("INTEGRAL".equals(paymentVo.getSysType())) {
                saveVipPo = true;
                vipPo.setIntegral(vipPo.getIntegral() - paymentPo.getIntegral());
                vipService.addIntegralLog(vipPo.getId(), vipPo.getCode(), -paymentPo.getIntegral(), "消费使用积分：" + po.getCode());
                if (vipPo.getIntegral() < 0) {
                    throw new MessageException("积分不足");
                }
            }
        }
        //增加积分和经验，及其相关的日志
        if (totalIntegral > 0) {
            vipPo.setIntegral(vipPo.getIntegral() + totalIntegral);
        }
        if (totalXp > 0) {
            vipPo.setXpValue(vipPo.getXpValue() + totalXp);
        }
        if (totalXp > 0 || totalIntegral > 0 || saveVipPo) {
            vipDao.save(vipPo);
        }
        return po;
    }

}
