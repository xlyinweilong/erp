package com.yin.erp.vip.xp.service;

import com.yin.erp.vip.xp.dao.VipXpRuleDao;
import com.yin.erp.vip.xp.dao.VipXpRuleGoodsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Integer calculateXp(String gradeId, String goodsId, String goodsBrandId, String goodsCategoryId, String goodsSeasonId, String goodsYearId) {
        return 0;
    }
}
