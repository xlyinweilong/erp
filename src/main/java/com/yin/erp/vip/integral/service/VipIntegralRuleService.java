package com.yin.erp.vip.integral.service;

import com.yin.erp.vip.integral.dao.VipIntegralRuleDao;
import com.yin.erp.vip.integral.dao.VipIntegralRuleGoodsDao;
import com.yin.erp.vip.integral.entity.po.VipIntegralRulePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 会员积分获取
 *
 * @author yin.weilong
 * @date 2019.02.17
 */
@Service
public class VipIntegralRuleService {

    @Autowired
    private VipIntegralRuleDao vipIntegralRuleDao;
    @Autowired
    private VipIntegralRuleGoodsDao vipIntegralRuleGoodsDao;

    /**
     * 获取积分
     *
     * @param gradeId
     * @param goodsId
     * @param goodsBrandId
     * @param goodsCategoryId
     * @param goodsSeasonId
     * @param goodsYearId
     * @return
     */
    public Integer calculateIntegral(String gradeId, Date now, String goodsId, String goodsBrandId, String goodsCategoryId, String goodsSeasonId, String goodsYearId) {
        List<VipIntegralRulePo> intergralRileList = vipIntegralRuleDao.findAllCanUse(gradeId, now);
        if (intergralRileList != null && !intergralRileList.isEmpty()) {
            for (VipIntegralRulePo vipIntegralRulePo : intergralRileList) {
                if (vipIntegralRulePo.getVipIntegralRuleGoodsPoList() == null || vipIntegralRulePo.getVipIntegralRuleGoodsPoList().isEmpty()) {
                    return vipIntegralRulePo.getIntegral();
                } else {
                    if (vipIntegralRulePo.getVipIntegralRuleGoodsPoList().stream().filter(g -> (g.getGoodsId() == null || g.getGoodsId().equals(goodsId))
                            && (g.getGoodsBrandId() == null || g.getGoodsBrandId().equals(goodsBrandId))
                            && (g.getGoodsCategoryId() == null || g.getGoodsCategoryId().equals(goodsCategoryId))
                            && (g.getGoodsSeasonId() == null || g.getGoodsSeasonId().equals(goodsSeasonId))
                            && (g.getGoodsYearId() == null || g.getGoodsYearId().equals(goodsYearId))
                    ).count() > 0L) {
                        return vipIntegralRulePo.getIntegral();
                    }
                }
            }
        }
        return 0;
    }
}
