package com.yin.erp.activity.controller;

import com.yin.erp.activity.dao.*;
import com.yin.erp.activity.entity.po.*;
import com.yin.erp.activity.entity.vo.ActivityVo;
import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.info.dict.entity.po.DictPo;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.vip.integral.entity.po.VipIntegralUpRuleGoodsPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 促销活动
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/activity")
@Transactional(rollbackFor = Throwable.class)
public class ActivityController {

    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private ActivityChannelDao activityChannelDao;
    @Autowired
    private ActivityVipDao activityVipDao;
    @Autowired
    private ActivityConditionGoodsDao activityConditionGoodsDao;
    @Autowired
    private ActivityGoodsDao activityGoodsDao;
    @Autowired
    private DictDao dictDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ActivityRuleDao activityRuleDao;

    /**
     * 保存
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@RequestBody ActivityVo vo) throws MessageException {
        ActivityPo po = new ActivityPo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = activityDao.findById(vo.getId()).get();
        } else {
            po.setCode("HD" + GenerateUtil.createSerialNumber());
        }
        po.setName(vo.getName());
        po.setMarks(vo.getMarks());
        po.setEndDate(vo.getEndDate());
        if (vo.getExecuteWeek() != null) {
            po.setExecuteWeek(StringUtils.join(vo.getExecuteWeek(), ","));
        }
        po.setPriority(vo.getPriority());
        po.setPoints(vo.getPoints());
        po.setStartDate(vo.getStartDate());
        po.setType(vo.getType());
        po.setStatus(vo.getStatus());
        po.setJoinChannelType(vo.getJoinChannelType());
        po.setJoinVipType(vo.getJoinVipType());
        po.setVipDiscountType(vo.getVipDiscountType());
        po.setJoinGoodsType(vo.getJoinGoodsType());
        po.setRuleType(vo.getRuleType());
        activityDao.save(po);
        activityChannelDao.deleteAllByActivityId(po.getId());
        if (vo.getActivityChannelList() != null) {
            for (ActivityChannelPo activityChannelPo : vo.getActivityChannelList()) {
                activityChannelPo.setActivityId(po.getId());
                ChannelPo channelPo = channelDao.findById(activityChannelPo.getChannelId()).get();
                activityChannelPo.setChannelName(channelPo.getName());
                activityChannelPo.setChannelCode(channelPo.getCode());
                activityChannelDao.save(activityChannelPo);
            }
        }
        activityVipDao.deleteAllByActivityId(po.getId());
        if (vo.getActivityVipList() != null) {
            for (ActivityVipPo activityVipPo : vo.getActivityVipList()) {
                activityVipPo.setActivityId(po.getId());
                activityVipDao.save(activityVipPo);
            }
        }
        activityGoodsDao.deleteAllByActivityId(po.getId());
        if (vo.getActivityGoodsList() != null) {
            for (ActivityGoodsPo activityGoodsPo : vo.getActivityGoodsList()) {
                activityGoodsPo.setActivityId(po.getId());
                if (StringUtils.isNotBlank(activityGoodsPo.getGoodsId())) {
                    GoodsPo goodsPo = goodsDao.findById(activityGoodsPo.getGoodsId()).get();
                    activityGoodsPo.setGoodsCode(goodsPo.getCode());
                    activityGoodsPo.setGoodsName(goodsPo.getName());
                    activityGoodsPo.setGoodsId(goodsPo.getId());
                }
                if (StringUtils.isNotBlank(activityGoodsPo.getGoodsBrandId())) {
                    DictPo dictPo = dictDao.findById(activityGoodsPo.getGoodsBrandId()).get();
                    activityGoodsPo.setGoodsBrandId(dictPo.getId());
                    activityGoodsPo.setGoodsBrandName(dictPo.getName());
                }
                if (StringUtils.isNotBlank(activityGoodsPo.getGoodsCategoryId())) {
                    DictPo dictPo = dictDao.findById(activityGoodsPo.getGoodsCategoryId()).get();
                    activityGoodsPo.setGoodsCategoryId(dictPo.getId());
                    activityGoodsPo.setGoodsCategoryName(dictPo.getName());
                }
                if (StringUtils.isNotBlank(activityGoodsPo.getGoodsYearId())) {
                    DictPo dictPo = dictDao.findById(activityGoodsPo.getGoodsYearId()).get();
                    activityGoodsPo.setGoodsYearId(dictPo.getId());
                    activityGoodsPo.setGoodsYearName(dictPo.getName());
                }
                if (StringUtils.isNotBlank(activityGoodsPo.getGoodsSeasonId())) {
                    DictPo dictPo = dictDao.findById(activityGoodsPo.getGoodsSeasonId()).get();
                    activityGoodsPo.setGoodsSeasonId(dictPo.getId());
                    activityGoodsPo.setGoodsSeasonName(dictPo.getName());
                }
                activityGoodsDao.save(activityGoodsPo);
            }
        }
        activityConditionGoodsDao.deleteAllByActivityId(po.getId());
        if (vo.getActivityConditionGoodsList() != null) {
            for (ActivityConditionGoodsPo activityConditionGoodsPo : vo.getActivityConditionGoodsList()) {
                activityConditionGoodsPo.setActivityId(po.getId());
                if (StringUtils.isNotBlank(activityConditionGoodsPo.getGoodsId())) {
                    GoodsPo goodsPo = goodsDao.findById(activityConditionGoodsPo.getGoodsId()).get();
                    activityConditionGoodsPo.setGoodsCode(goodsPo.getCode());
                    activityConditionGoodsPo.setGoodsName(goodsPo.getName());
                    activityConditionGoodsPo.setGoodsId(goodsPo.getId());
                }
                if (StringUtils.isNotBlank(activityConditionGoodsPo.getGoodsBrandId())) {
                    DictPo dictPo = dictDao.findById(activityConditionGoodsPo.getGoodsBrandId()).get();
                    activityConditionGoodsPo.setGoodsBrandId(dictPo.getId());
                    activityConditionGoodsPo.setGoodsBrandName(dictPo.getName());
                }
                if (StringUtils.isNotBlank(activityConditionGoodsPo.getGoodsCategoryId())) {
                    DictPo dictPo = dictDao.findById(activityConditionGoodsPo.getGoodsCategoryId()).get();
                    activityConditionGoodsPo.setGoodsCategoryId(dictPo.getId());
                    activityConditionGoodsPo.setGoodsCategoryName(dictPo.getName());
                }
                if (StringUtils.isNotBlank(activityConditionGoodsPo.getGoodsYearId())) {
                    DictPo dictPo = dictDao.findById(activityConditionGoodsPo.getGoodsYearId()).get();
                    activityConditionGoodsPo.setGoodsYearId(dictPo.getId());
                    activityConditionGoodsPo.setGoodsYearName(dictPo.getName());
                }
                if (StringUtils.isNotBlank(activityConditionGoodsPo.getGoodsSeasonId())) {
                    DictPo dictPo = dictDao.findById(activityConditionGoodsPo.getGoodsSeasonId()).get();
                    activityConditionGoodsPo.setGoodsSeasonId(dictPo.getId());
                    activityConditionGoodsPo.setGoodsSeasonName(dictPo.getName());
                }
                activityConditionGoodsDao.save(activityConditionGoodsPo);
            }
        }
        activityRuleDao.deleteAllByActivityId(po.getId());
        if (vo.getActivityRuleList() != null) {
            for (ActivityRulePo activityRulePo : vo.getActivityRuleList()) {
                activityRulePo.setActivityId(po.getId());
                activityRuleDao.save(activityRulePo);
            }
        }
        return BaseJson.getSuccess();
    }

    /**
     * 获取
     *
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "list")
    public BaseJson list(ActivityVo vo) throws MessageException {
        Page<ActivityPo> page = activityDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getSearchKey())) {
                List<Predicate> predicatesSearch = new ArrayList<>();
                predicatesSearch.add(criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%"));
                predicatesSearch.add(criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%"));
                predicates.add(criteriaBuilder.or(predicatesSearch.toArray(new Predicate[predicatesSearch.size()])));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        return BaseJson.getSuccess(page);
    }

    @GetMapping(value = "info")
    public BaseJson info(String id) throws MessageException {
        ActivityPo po = activityDao.findById(id).get();
        ActivityVo vo = new ActivityVo();
        vo.setId(po.getId());
        vo.setCode(po.getCode());
        vo.setEndDate(po.getEndDate());
        if (StringUtils.isNotBlank(po.getExecuteWeek())) {
            vo.setExecuteWeek(po.getExecuteWeek().split(","));
        }
        vo.setRuleType(po.getRuleType());
        vo.setMarks(po.getMarks());
        vo.setName(po.getName());
        vo.setPoints(po.getPoints());
        vo.setStartDate(po.getStartDate());
        vo.setStatus(po.getStatus());
        vo.setPriority(po.getPriority());
        vo.setType(po.getType());
        vo.setJoinChannelType(po.getJoinChannelType());
        vo.setJoinVipType(po.getJoinVipType());
        vo.setVipDiscountType(po.getVipDiscountType());
        vo.setJoinGoodsType(po.getJoinGoodsType());
        vo.setRuleType(po.getRuleType());
        vo.setActivityChannelList(activityChannelDao.findByActivityId(vo.getId()));
        vo.setActivityVipList(activityVipDao.findByActivityId(vo.getId()));
        vo.setActivityGoodsList(activityGoodsDao.findByActivityId(vo.getId()));
        vo.setActivityConditionGoodsList(activityConditionGoodsDao.findByActivityId(vo.getId()));
        vo.setActivityRuleList(activityRuleDao.findByActivityId(vo.getId()));
        return BaseJson.getSuccess(vo);
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            activityDao.deleteById(id);
        }
        return BaseJson.getSuccess();
    }

}
