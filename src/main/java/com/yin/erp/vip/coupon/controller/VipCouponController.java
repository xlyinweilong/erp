package com.yin.erp.vip.coupon.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.exceptions.MessageException;
import com.yin.common.service.LoginService;
import com.yin.common.utils.GenerateUtil;
import com.yin.erp.vip.coupon.dao.VipCouponDao;
import com.yin.erp.vip.coupon.entity.po.VipCouponPo;
import com.yin.erp.vip.coupon.entity.vo.in.CouponVo;
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
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@RequestBody CouponVo vo, HttpServletRequest request) throws MessageException {
        UserSessionBo user = loginService.getUserSession(request);
        if (vo.getCreateCount() == null) {
            vo.setCreateCount(1);
        }
        for (int i = 0; i < vo.getCreateCount(); i++) {
            VipCouponPo po = new VipCouponPo();
            if (StringUtils.isNotBlank(vo.getId())) {
                po = vipCouponDao.findById(vo.getId()).get();
                if (po.isUsed()) {
                    throw new MessageException(po.getCode() + "已经使用，无法修改");
                }
            } else {
                po.setCode("DYJ" + GenerateUtil.createSerialNumber());
            }
            po.setStartDate(vo.getStartDate());
            po.setEndDate(vo.getEndDate());
            po.setAmount(vo.getAmount());
            po.setConditionAmount(vo.getConditionAmount());
            po.setDiscount(vo.getDiscount());
            po.setType(vo.getType());
            po.setVipName(null);
            po.setVipCode(null);
            po.setVipId(null);
            if (StringUtils.isNotBlank(po.getVipId())) {
                VipPo vip = vipDao.findById(po.getVipId()).get();
                po.setVipName(vip.getName());
                po.setVipCode(vip.getCode());
                po.setVipId(vip.getId());
            }
            vipCouponDao.save(po);
        }
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
