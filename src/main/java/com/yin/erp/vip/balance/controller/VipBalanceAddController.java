package com.yin.erp.vip.balance.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.user.user.service.LoginService;
import com.yin.erp.vip.balance.dao.VipBalanceAddDao;
import com.yin.erp.vip.balance.entity.po.VipBalanceAddPo;
import com.yin.erp.vip.info.dao.VipDao;
import com.yin.erp.vip.info.entity.po.VipPo;
import com.yin.erp.vip.integral.entity.vo.VipIntegralUpRuleVo;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员余额充值
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/vip/balance_add")
@Transactional(rollbackFor = Throwable.class)
public class VipBalanceAddController {

    @Autowired
    private VipDao vipDao;
    @Autowired
    private VipBalanceAddDao vipBalanceAddDao;
    @Autowired
    private LoginService loginService;


    /**
     * 保存
     *
     * @param po
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@RequestBody VipBalanceAddPo po, HttpServletRequest request) throws MessageException {
        UserSessionBo user = loginService.getUserSession(request);
        po.setId(GenerateUtil.createUUID());
        po.setCreateUserId(user.getId());
        po.setCreateUserName(user.getName());
        VipPo vip = vipDao.findById(po.getVipId()).get();
        po.setVipName(vip.getName());
        po.setVipCode(vip.getCode());
        if (po.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            vip.setBalance(vip.getBalance().add(po.getBalance()));
        }
        if (po.getIntegral() > 0) {
            vip.setIntegral(vip.getIntegral() + po.getIntegral());
        }
        if (po.getXp() > 0) {
            vip.setXpValue(vip.getXpValue() + po.getXp());
        }
        vipDao.save(vip);
        vipBalanceAddDao.save(po);
        return BaseJson.getSuccess();
    }

    /**
     * 获取
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "list")
    public BaseJson list(VipIntegralUpRuleVo vo) throws MessageException {
        Page<VipBalanceAddPo> page = vipBalanceAddDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(vo.getSearchKey())) {
                predicates.add(criteriaBuilder.like(root.get("vipCode"), "%" + vo.getSearchKey() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        return BaseJson.getSuccess(page);
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo, HttpServletRequest request) throws MessageException {
        UserSessionBo user = loginService.getUserSession(request);
        for (String id : vo.getIds()) {
            VipBalanceAddPo po = vipBalanceAddDao.findById(id).get();
            po.setInvalid(true);
            po.setInvalidUserId(user.getId());
            po.setInvalidUserName(user.getName());
            VipPo vip = vipDao.findById(po.getVipId()).get();
            vip.setBalance(vip.getBalance().subtract(po.getBalance()));
            vip.setIntegral(vip.getIntegral() - po.getIntegral());
            vip.setXpValue(vip.getXpValue() - po.getXp());
            vipDao.save(vip);
            vipBalanceAddDao.save(po);
        }
        return BaseJson.getSuccess();
    }


}
