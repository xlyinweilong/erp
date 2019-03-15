package com.yin.erp.vip.xp.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.vip.common.vo.BaseVipSearchVo;
import com.yin.erp.vip.xp.dao.VipXpLogDao;
import com.yin.erp.vip.xp.entity.po.VipXpLogPo;
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
@RequestMapping(value = "api/vip/xp_log")
@Transactional(rollbackFor = Throwable.class)
public class VipXpLogController {

    @Autowired
    private VipXpLogDao vipXpLogDao;



    /**
     * 获取
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "list")
    public BaseJson list(BaseVipSearchVo vo) throws MessageException {
        Page<VipXpLogPo> page = vipXpLogDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(vo.getSearchKey())) {
                predicates.add(criteriaBuilder.like(root.get("vipCode"), "%" + vo.getSearchKey() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        return BaseJson.getSuccess(page);
    }


}
