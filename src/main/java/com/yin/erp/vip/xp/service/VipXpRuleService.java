package com.yin.erp.vip.xp.service;

import com.yin.erp.vip.xp.dao.VipXpRuleDao;
import com.yin.erp.vip.xp.dao.VipXpRuleGoodsDao;
import com.yin.erp.vip.xp.entity.po.VipXpRulePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 会员经验获取
 *
 * @author yin.weilong
 * @date 2019.02.17
 */
@Service
public class VipXpRuleService {

    @Autowired
    private VipXpRuleDao vipXpRuleDao;
    @Autowired
    private VipXpRuleGoodsDao vipXpRuleGoodsDao;

    /**
     * 获取经验
     *
     * @param gradeId
     * @param goodsId
     * @param goodsBrandId
     * @param goodsCategoryId
     * @param goodsSeasonId
     * @param goodsYearId
     * @return
     */
    public Integer calculateXp(String gradeId, Date now, String goodsId, String goodsBrandId, String goodsCategoryId, String goodsSeasonId, String goodsYearId) {
        //根据经验获取当前等级
        List<VipXpRulePo> xpRileList = vipXpRuleDao.findAllCanUse(gradeId, now);
        if (xpRileList != null && !xpRileList.isEmpty()) {
            for (VipXpRulePo vipXpRulePo : xpRileList) {
                if (vipXpRulePo.getVipXpRuleGoodsPoList() == null || vipXpRulePo.getVipXpRuleGoodsPoList().isEmpty()) {
                    return vipXpRulePo.getPriority();
                } else {
                    if (vipXpRulePo.getVipXpRuleGoodsPoList().stream().filter(g -> (g.getGoodsId() == null || g.getGoodsId().equals(goodsId))
                            && (g.getGoodsBrandId() == null || g.getGoodsBrandId().equals(goodsBrandId))
                            && (g.getGoodsCategoryId() == null || g.getGoodsCategoryId().equals(goodsCategoryId))
                            && (g.getGoodsSeasonId() == null || g.getGoodsSeasonId().equals(goodsSeasonId))
                            && (g.getGoodsYearId() == null || g.getGoodsYearId().equals(goodsYearId))
                    ).count() > 0L) {
                        return vipXpRulePo.getPriority();
                    }
                }
            }
        }
        return 0;
    }
}
