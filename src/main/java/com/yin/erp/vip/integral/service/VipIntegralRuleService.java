package com.yin.erp.vip.integral.service;

import com.yin.erp.vip.integral.dao.VipIntegralRuleDao;
import com.yin.erp.vip.integral.dao.VipIntegralRuleGoodsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Integer calculateIntegral(String gradeId, String goodsId, String goodsBrandId, String goodsCategoryId, String goodsSeasonId, String goodsYearId) {
        return 0;
    }
}
