package com.yin.erp.pos.cash.controller;

import com.yin.erp.activity.dao.*;
import com.yin.erp.activity.entity.po.ActivityPo;
import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.pos.cash.entity.vo.in.PayVo;
import com.yin.erp.pos.cash.entity.vo.out.PosActivityVo;
import com.yin.erp.pos.cash.entity.vo.out.PosVipVo;
import com.yin.erp.pos.cash.service.CashService;
import com.yin.erp.user.user.service.LoginService;
import com.yin.erp.vip.grade.entity.po.VipGradePo;
import com.yin.erp.vip.grade.service.VipGradeService;
import com.yin.erp.vip.info.dao.VipDao;
import com.yin.erp.vip.info.entity.po.VipPo;
import com.yin.erp.vip.integral.dao.VipIntegralToAmountDao;
import com.yin.erp.vip.integral.entity.po.VipIntegralToAmountPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 销售控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/pos/cash")
@Transactional(rollbackFor = Throwable.class)
public class CashController {

    @Autowired
    private CashService cashService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private VipGradeService vipGradeService;
    @Autowired
    private VipDao vipDao;
    @Autowired
    private ActivityDao activityDao;
    @Autowired
    private ActivityChannelDao activityChannelDao;
    @Autowired
    private ActivityRuleDao activityRuleDao;
    @Autowired
    private ActivityRuleGoodsDao activityRuleGoodsDao;
    @Autowired
    private ActivityRuleRangeDao activityRuleRangeDao;
    @Autowired
    private ActivityVipDao activityVipDao;
    @Autowired
    private ActivityConditionGoodsDao activityConditionGoodsDao;
    @Autowired
    private ActivityGoodsDao activityGoodsDao;
    @Autowired
    private VipIntegralToAmountDao vipIntegralToAmountDao;

    //查询销售

    /**
     * 查询会员
     *
     * @param code
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "vip")
    public BaseJson vipInfo(String code) throws MessageException {
        VipPo vipPo = vipDao.findByCode(code);
        if (vipPo == null) {
            throw new MessageException("不存在会员编号：" + code);
        }
        PosVipVo posVipVo = new PosVipVo();
        posVipVo.setId(vipPo.getId());
        posVipVo.setCode(vipPo.getCode());
        posVipVo.setName(vipPo.getName());
        posVipVo.setBalance(vipPo.getBalance());
        //获取会员的等级
        VipGradePo vipGradePo = vipGradeService.getGradeByVip(posVipVo.getXpValue());
        posVipVo.setDiscount(vipGradePo.getDiscount());
        posVipVo.setGradeId(vipGradePo.getId());
        //等级多少积分为1元
        VipIntegralToAmountPo vipIntegralToAmountPo = vipIntegralToAmountDao.findByGradeId(vipGradePo.getId());
        if(vipIntegralToAmountPo != null){
            posVipVo.setIntegralToMoney(vipIntegralToAmountPo.getIntegral());
        }
        return BaseJson.getSuccess(posVipVo);
    }


    /**
     * 查询促销活动
     *
     * @param channelId
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "activity_list")
    public BaseJson activityList(String channelId) throws MessageException {
        //当前时间
        Date now = new Date();
        //今天是星期几
        Integer week = 1;
        //查询指定的渠道编号
        List activityIds = activityChannelDao.findDistinctActivityIdByChannelId(channelId);
        List<ActivityPo> activityList = activityDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("startDate"), now));
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), now));
            predicates.add(criteriaBuilder.equal(root.get("status"), "PENDING"));
            //渠道
            List<Predicate> predicatesChannel = new ArrayList<>();
            predicatesChannel.add(criteriaBuilder.equal(root.get("joinChannelType"), "ALL"));
            predicatesChannel.add(criteriaBuilder.in(root.get("id")).value(activityIds));
            predicates.add(criteriaBuilder.or(predicatesChannel.toArray(new Predicate[predicatesChannel.size()])));
            //星期
            List<Predicate> predicatesWeek = new ArrayList<>();
            predicatesWeek.add(criteriaBuilder.equal(root.get("executeWeek"), ""));
            predicatesWeek.add(criteriaBuilder.like(root.get("executeWeek"), "%" + week + "%"));
            predicates.add(criteriaBuilder.or(predicatesWeek.toArray(new Predicate[predicatesWeek.size()])));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, new Sort(Sort.Direction.DESC, "priority", "createDate"));
        List<PosActivityVo> list = new ArrayList<>();
        activityList.forEach(a -> {
            PosActivityVo pav = new PosActivityVo();
            pav.setCode(a.getCode());
            pav.setId(a.getId());
            pav.setJoinChannelType(a.getJoinChannelType());
            pav.setJoinGoodsType(a.getJoinGoodsType());
            pav.setJoinVipType(a.getJoinVipType());
            pav.setMarks(a.getMarks());
            pav.setName(a.getName());
            pav.setPriority(a.getPriority());
            pav.setRuleType(a.getRuleType());
            pav.setType(a.getType());
            pav.setActivityVipList(activityVipDao.findByActivityId(a.getId()));
            pav.setActivityGoodsList(activityGoodsDao.findByActivityId(a.getId()));
            pav.setActivityConditionGoodsList(activityConditionGoodsDao.findByActivityId(a.getId()));
            pav.setActivityRuleList(activityRuleDao.findByActivityId(a.getId()));
            pav.setActivityRuleGoodsList(activityRuleGoodsDao.findByActivityId(a.getId()));
            pav.setActivityRuleRangeList(activityRuleRangeDao.findByActivityId(a.getId()));
            list.add(pav);
        });
        return BaseJson.getSuccess(list);
    }

    /**
     * 支付
     *
     * @param payVo
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "pay", consumes = "application/json")
    public BaseJson pay(@RequestBody PayVo payVo, HttpServletRequest request) throws Exception {
        UserSessionBo user = loginService.getUserSession(request);
        cashService.pay(payVo, user);
        return BaseJson.getSuccess();
    }


}
