package com.yin.erp.activity.controller;

import com.yin.erp.activity.dao.*;
import com.yin.erp.activity.entity.po.*;
import com.yin.erp.activity.entity.vo.ActivityVo;
import com.yin.erp.activity.service.ActivityService;
import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BaseUploadMessage;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.info.dict.entity.po.DictPo;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.marketpoint.dao.MarketPointDao;
import com.yin.erp.info.marketpoint.entity.po.MarketPointPo;
import com.yin.erp.user.user.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private ActivityRuleGoodsDao activityRuleGoodsDao;
    @Autowired
    private ActivityRuleRangeDao activityRuleRangeDao;
    @Autowired
    private MarketPointDao marketPointDao;
    @Autowired
    private LoginService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ActivityService activityService;

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
        po.setMarketPointId(null);
        po.setMarketPointCode(null);
        if (StringUtils.isNotBlank(vo.getMarketPointId())) {
            MarketPointPo marketPointPo = marketPointDao.findById(vo.getMarketPointId()).get();
            po.setMarketPointId(marketPointPo.getId());
            po.setMarketPointCode(marketPointPo.getCode());
        }
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
        activityRuleGoodsDao.deleteAllByActivityId(po.getId());
        activityRuleRangeDao.deleteAllByActivityId(po.getId());
        if (vo.getActivityRuleGoodsList() != null) {
            for (ActivityRuleGoodsPo activityRuleGoodsPo : vo.getActivityRuleGoodsList()) {
                activityRuleGoodsPo.setActivityId(po.getId());
                if (StringUtils.isNotBlank(activityRuleGoodsPo.getGoodsId())) {
                    GoodsPo goodsPo = goodsDao.findById(activityRuleGoodsPo.getGoodsId()).get();
                    activityRuleGoodsPo.setGoodsCode(goodsPo.getCode());
                    activityRuleGoodsPo.setGoodsName(goodsPo.getName());
                    activityRuleGoodsPo.setGoodsId(goodsPo.getId());
                }
                activityRuleGoodsDao.save(activityRuleGoodsPo);
            }
        }
        if (vo.getActivityRuleRangeList() != null) {
            for (ActivityRuleRangePo activityRuleRangePo : vo.getActivityRuleRangeList()) {
                activityRuleRangePo.setActivityId(po.getId());
                if (StringUtils.isNotBlank(activityRuleRangePo.getGoodsBrandId())) {
                    DictPo dictPo = dictDao.findById(activityRuleRangePo.getGoodsBrandId()).get();
                    activityRuleRangePo.setGoodsBrandId(dictPo.getId());
                    activityRuleRangePo.setGoodsBrandName(dictPo.getName());
                }
                if (StringUtils.isNotBlank(activityRuleRangePo.getGoodsCategoryId())) {
                    DictPo dictPo = dictDao.findById(activityRuleRangePo.getGoodsCategoryId()).get();
                    activityRuleRangePo.setGoodsCategoryId(dictPo.getId());
                    activityRuleRangePo.setGoodsCategoryName(dictPo.getName());
                }
                if (StringUtils.isNotBlank(activityRuleRangePo.getGoodsYearId())) {
                    DictPo dictPo = dictDao.findById(activityRuleRangePo.getGoodsYearId()).get();
                    activityRuleRangePo.setGoodsYearId(dictPo.getId());
                    activityRuleRangePo.setGoodsYearName(dictPo.getName());
                }
                if (StringUtils.isNotBlank(activityRuleRangePo.getGoodsSeasonId())) {
                    DictPo dictPo = dictDao.findById(activityRuleRangePo.getGoodsSeasonId()).get();
                    activityRuleRangePo.setGoodsSeasonId(dictPo.getId());
                    activityRuleRangePo.setGoodsSeasonName(dictPo.getName());
                }
                activityRuleRangeDao.save(activityRuleRangePo);
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
        vo.setMarketPointId(po.getMarketPointId());
        vo.setMarketPointCode(po.getMarketPointCode());
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
        vo.setActivityRuleGoodsList(activityRuleGoodsDao.findByActivityId(vo.getId()));
        vo.setActivityRuleRangeList(activityRuleRangeDao.findByActivityId(vo.getId()));
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


    /**
     * 上传促销货品
     *
     * @param file
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/upload_rule_price_promotion")
    public BaseJson uploadRulePricePromotion(@RequestParam("file") MultipartFile file, javax.servlet.http.HttpServletRequest request) throws Exception {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        ValueOperations operations = redisTemplate.opsForValue();
        LocalDateTime startTime = LocalDateTime.now();
        operations.set(userSessionBo.getId() + ":upload:activity", new BaseUploadMessage(), 10L, TimeUnit.MINUTES);
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            activityService.uploadGoods(workbook, userSessionBo, startTime, true);
        } catch (Throwable e) {
            operations.set(userSessionBo.getId() + ":upload:activity", new BaseUploadMessage(-1, TimeUtil.useTime(startTime), e.getMessage()), 10L, TimeUnit.MINUTES);
            e.printStackTrace();
        }
        return BaseJson.getSuccess("文件上传成功");
    }

    /**
     * 上传货品
     *
     * @param file
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/upload_goods")
    public BaseJson uploadGoods(@RequestParam("file") MultipartFile file, javax.servlet.http.HttpServletRequest request) throws Exception {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        ValueOperations operations = redisTemplate.opsForValue();
        LocalDateTime startTime = LocalDateTime.now();
        operations.set(userSessionBo.getId() + ":upload:activity", new BaseUploadMessage(), 10L, TimeUnit.MINUTES);
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            activityService.uploadGoods(workbook, userSessionBo, startTime, false);
        } catch (Throwable e) {
            operations.set(userSessionBo.getId() + ":upload:activity", new BaseUploadMessage(-1, TimeUtil.useTime(startTime), e.getMessage()), 10L, TimeUnit.MINUTES);
            e.printStackTrace();
        }
        return BaseJson.getSuccess("文件上传成功");
    }


}
