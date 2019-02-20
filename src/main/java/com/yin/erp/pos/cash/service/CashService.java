package com.yin.erp.pos.cash.service;

import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.pos.cash.dao.PosCashDao;
import com.yin.erp.pos.cash.dao.PosCashDetailDao;
import com.yin.erp.pos.cash.dao.PosCashPaymentDao;
import com.yin.erp.pos.cash.entity.po.PosCashDetailPo;
import com.yin.erp.pos.cash.entity.po.PosCashPaymentPo;
import com.yin.erp.pos.cash.entity.po.PosCashPo;
import com.yin.erp.pos.cash.entity.vo.in.PayVo;
import com.yin.erp.pos.cash.entity.vo.in.PosCashGoods;
import com.yin.erp.pos.cash.entity.vo.in.PosCashPayment;
import com.yin.erp.stock.entity.bo.StockBo;
import com.yin.erp.stock.service.StockChannelService;
import com.yin.erp.vip.integral.service.VipIntegralRuleService;
import com.yin.erp.vip.xp.service.VipXpRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private PosCashDetailDao posCashDetailDao;
    @Autowired
    private PosCashPaymentDao posCashPaymentDao;
    @Autowired
    private StockChannelService stockChannelService;
    @Autowired
    private VipIntegralRuleService vipIntegralRuleService;
    @Autowired
    private VipXpRuleService vipXpRuleService;


    /**
     * 支付
     *
     * @param payVo
     * @param user
     */
    public void pay(PayVo payVo, UserSessionBo user) throws MessageException {
        //保存数据
        this.savePo(payVo, user);
        //调整库存
        for (PosCashGoods goodsVo : payVo.getGoodsList()) {
            stockChannelService.minus(new StockBo(payVo.getChannelId(), null, goodsVo.getId(), goodsVo.getGoodsColorId(), goodsVo.getGoodsSizeId(), goodsVo.getBillCount()));
        }

    }

    /**
     * 保存单据信息
     *
     * @param payVo
     * @param user
     */
    public void savePo(PayVo payVo, UserSessionBo user) {
        Date now = new Date();
        //保存单据
        PosCashPo po = new PosCashPo();
        po.setCode("POS" + GenerateUtil.createSerialNumber());
        po.setBillDate(now);
        po.setAuditDate(now);
        po.setAuditUserId(user.getId());
        po.setAuditUserName(user.getName());
        po.setChannelId(payVo.getChannelId());
        po.setCreateUserId(user.getId());
        po.setCreateUserName(user.getName());
        po.setChannelName(payVo.getChannelName());
        po.setChannelCode(payVo.getChannelCode());
        po.setStatus(BillStatusEnum.AUDITED.name());
        po.setVipCode(payVo.getVipCode());
        po.setVipId(payVo.getVipId());
        po.setVipName(payVo.getVipName());
        po.setTotalAmount(payVo.getGoodsList().stream().map(g -> g.getAmount()).reduce((a, b) -> a.add(b)).get());
        po.setTotalCount(payVo.getGoodsList().stream().map(g -> g.getBillCount()).reduce((a, b) -> a + b).get());
        po.setTotalTagAmount(payVo.getGoodsList().stream().map(g -> g.getTagPrice()).reduce((a, b) -> a.add(b)).get());
        posCashDao.save(po);
        //保存详情
        for (PosCashGoods goodsVo : payVo.getGoodsList()) {
            PosCashDetailPo detailPo = new PosCashDetailPo();
            detailPo.setStatus(BillStatusEnum.AUDITED.name());
            detailPo.setRemarks(StringUtils.trimToNull(goodsVo.getRemarks()));
            detailPo.setDiyPrice(goodsVo.getDiyPrice());
            detailPo.setActivityId(StringUtils.trimToNull(goodsVo.getActivityId()));
            detailPo.setVipDiscount(goodsVo.getVipDiscount());
            detailPo.setAmount(goodsVo.getAmount());
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
            detailPo.setEmployCode(StringUtils.trimToNull(goodsVo.getEmployCode()));
            detailPo.setEmployId(StringUtils.trimToNull(goodsVo.getEmployId()));
            detailPo.setEmployName(StringUtils.trimToNull(goodsVo.getEmployCode()));
            if(po.getVipId() != null){
                //获取积分
                Integer integral = vipIntegralRuleService.calculateIntegral(payVo.getVipGradeId(),detailPo.getGoodsId(),goodsVo.getGoodsBrandId(),goodsVo.getGoodsCategoryId(),goodsVo.getGoodsSeasonId(),goodsVo.getGoodsYearId());
                detailPo.setIntegral(integral);
                //获得经验
                Integer xp = vipXpRuleService.calculateXp(payVo.getVipGradeId(),detailPo.getGoodsId(),goodsVo.getGoodsBrandId(),goodsVo.getGoodsCategoryId(),goodsVo.getGoodsSeasonId(),goodsVo.getGoodsYearId());
                detailPo.setXp(xp);
            }
            posCashDetailDao.save(detailPo);
        }
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
        }
    }

}
