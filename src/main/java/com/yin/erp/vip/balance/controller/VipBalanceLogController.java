package com.yin.erp.vip.balance.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.vip.balance.dao.VipBalanceLogDao;
import com.yin.erp.vip.balance.entity.po.VipBalanceLogPo;
import com.yin.erp.vip.common.vo.BaseVipSearchVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员余额充值
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/vip/balance_log")
@Transactional(rollbackFor = Throwable.class)
public class VipBalanceLogController {

    @Autowired
    private VipBalanceLogDao vipBalanceLogDao;



    /**
     * 获取
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "list")
    public BaseJson list(BaseVipSearchVo vo) throws MessageException {
        Page<VipBalanceLogPo> page = vipBalanceLogDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(vo.getSearchKey())) {
                predicates.add(criteriaBuilder.like(root.get("vipCode"), "%" + vo.getSearchKey() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        return BaseJson.getSuccess(page);
    }


}
