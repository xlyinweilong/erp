package com.yin.erp.info.marketpoint.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.exceptions.MessageException;
import com.yin.common.service.LoginService;
import com.yin.erp.info.marketpoint.dao.MarketPointDao;
import com.yin.erp.info.marketpoint.entity.po.MarketPointPo;
import com.yin.erp.info.marketpoint.entity.vo.MarketPointVo;
import com.yin.erp.info.marketpoint.service.MarketPointService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 条形码制器
 *
 * @author yin
 */
@RestController
@Transactional(rollbackFor = Throwable.class)
@RequestMapping(value = "api/info/marketPoint")
public class MarketPointController {

    @Autowired
    private MarketPointService marketPointService;
    @Autowired
    private MarketPointDao marketPointDao;
    @Autowired
    private LoginService userService;

    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody MarketPointVo vo, HttpServletRequest request) throws Exception {
        UserSessionBo user = userService.getUserSession(request);
        marketPointService.save(vo, user);
        return BaseJson.getSuccess();
    }

    /**
     * 列表
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(MarketPointVo vo, HttpServletRequest request) {
        Page<MarketPointPo> page = marketPointDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNotBlank(vo.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getName() + "%"));
            }
            if (StringUtils.isNotBlank(vo.getSearchKey())) {
                List<Predicate> predicatesSearch = new ArrayList<>();
                predicatesSearch.add(criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%"));
                predicatesSearch.add(criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%"));
                predicates.add(criteriaBuilder.or(predicatesSearch.toArray(new Predicate[predicatesSearch.size()])));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        return BaseJson.getSuccess(page);
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) throws MessageException {
        return BaseJson.getSuccess(marketPointService.findById(id));
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws MessageException {
        marketPointService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

}
