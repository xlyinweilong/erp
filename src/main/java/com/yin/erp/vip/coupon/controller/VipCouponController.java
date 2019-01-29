package com.yin.erp.vip.coupon.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.user.user.service.LoginService;
import com.yin.erp.vip.coupon.dao.VipCouponDao;
import com.yin.erp.vip.coupon.entity.po.VipCouponPo;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 代用卷
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/vip/coupon")
@Transactional(rollbackFor = Throwable.class)
public class VipCouponController {

    @Autowired
    private VipDao vipDao;
    @Autowired
    private VipCouponDao vipCouponDao;
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
    public BaseJson save(@RequestBody VipCouponPo po, HttpServletRequest request) throws MessageException {
        UserSessionBo user = loginService.getUserSession(request);
        if (StringUtils.isBlank(po.getId())) {
            po.setCode("DYJ" + GenerateUtil.createSerialNumber());
            po.setId(GenerateUtil.createUUID());
        }
//        po.setCreateUserId(user.getId());
//        po.setCreateUserName(user.getName());
        if (StringUtils.isNotBlank(po.getVipId())) {
            VipPo vip = vipDao.findById(po.getVipId()).get();
            po.setVipName(vip.getName());
            po.setVipCode(vip.getCode());
        } else {
            po.setVipId(null);
        }
        vipCouponDao.save(po);
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
        Page<VipCouponPo> page = vipCouponDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(vo.getSearchKey())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%"));
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
            vipCouponDao.deleteById(id);
        }
        return BaseJson.getSuccess();
    }


}
