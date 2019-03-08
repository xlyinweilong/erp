package com.yin.erp.bill.settlement.controller;


import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.bill.settlement.dao.SettlementDao;
import com.yin.erp.bill.settlement.entity.po.SettlementPo;
import com.yin.erp.bill.settlement.entity.vo.SearchSettlementVo;
import com.yin.erp.bill.settlement.service.SettlementService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 盘点结存
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/bill/settlement")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private SettlementDao settlementDao;

    /**
     * 列表
     *
     * @return
     */
    @PostMapping(value = "list", consumes = "application/json")
    public BaseJson list(@RequestBody SearchSettlementVo vo) throws MessageException {
        Page<SettlementPo> page = settlementDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getType())) {
                predicates.add(criteriaBuilder.like(root.get("type"), "%" + vo.getType() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSearchKey())) {
                Predicate p1 = criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%");
                Predicate p2 = null;
                Predicate p3 = null;
                if (vo.getType().equals("WAREHOUSE")) {
                    p2 = criteriaBuilder.like(root.get("warehouseCode"), "%" + vo.getSearchKey() + "%");
                    p3 = criteriaBuilder.like(root.get("warehouseName"), "%" + vo.getSearchKey() + "%");
                } else {
                    p2 = criteriaBuilder.like(root.get("channelCode"), "%" + vo.getSearchKey() + "%");
                    p3 = criteriaBuilder.like(root.get("channelName"), "%" + vo.getSearchKey() + "%");
                }
                Predicate predicatesPermission = criteriaBuilder.or(p1, p2, p3);
                predicates.add(predicatesPermission);
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
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws MessageException {
        settlementService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

}
