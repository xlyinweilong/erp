package com.yin.erp.stock.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.stock.dao.StockWarehouseDao;
import com.yin.erp.stock.entity.po.StockWarehousePo;
import com.yin.erp.stock.entity.vo.StockVo;
import com.yin.erp.stock.service.StockWarehouseService;
import com.yin.erp.user.user.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 渠道制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/stock/warehouse")
public class StockWarehouseController {

    @Autowired
    private StockWarehouseService stockWarehouseService;
    @Autowired
    private StockWarehouseDao stockWarehouseDao;
    @Autowired
    private LoginService loginService;
    @Autowired
    private EntityManager em;


    /**
     * stock_info
     * 渠道库存详情
     *
     * @return
     */
    @GetMapping(value = "stock_info")
    public BaseJson stockInfo(String warehouseId, String goodsId, String goodsColorId, String goodsSizeId) {
        return BaseJson.getSuccess(stockWarehouseDao.findByWarehouseIdAndGoodsIdAndGoodsColorIdAndGoodsSizeId(warehouseId, goodsId, goodsColorId, goodsSizeId));
    }

    @GetMapping(value = "stock_list")
    public BaseJson stockList(StockVo stockVo, HttpServletRequest request) {
        UserSessionBo user = loginService.getUserSession(request);
        Page<StockWarehousePo> page = stockWarehouseDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.notEqual(root.get("stockCount"), 0));
            if (StringUtils.isNotBlank(stockVo.getWarehouseCode())) {
                predicates.add(criteriaBuilder.equal(root.get("warehouseCode"), stockVo.getWarehouseCode()));
            }
            if (StringUtils.isNotBlank(stockVo.getGoodsCode())) {
                predicates.add(criteriaBuilder.equal(root.get("goodsCode"), stockVo.getGoodsCode()));
            }
            if (StringUtils.isNotBlank(stockVo.getGoodsColorCode())) {
                predicates.add(criteriaBuilder.equal(root.get("goodsColorCode"), stockVo.getGoodsColorCode()));
            }
            if (StringUtils.isNotBlank(stockVo.getGoodsColorName())) {
                predicates.add(criteriaBuilder.equal(root.get("goodsColorName"), stockVo.getGoodsColorName()));
            }
            if (StringUtils.isNotBlank(stockVo.getGoodsSizeName())) {
                predicates.add(criteriaBuilder.equal(root.get("goodsSizeName"), stockVo.getGoodsSizeName()));
            }
            if (StringUtils.isNotBlank(stockVo.getGoodsSizeName())) {
                predicates.add(criteriaBuilder.equal(root.get("goodsSizeName"), stockVo.getGoodsSizeName()));
            }
            Predicate p1 = criteriaBuilder.isNull(root.get("goodsGroupId"));
            if (!user.getGoodsGroupIds().isEmpty()) {
                predicates.add(criteriaBuilder.or(p1, criteriaBuilder.in(root.get("goodsGroupId")).value(user.getGoodsGroupIds())));
            } else {
                predicates.add(p1);
            }
            Predicate p2 = criteriaBuilder.isNull(root.get("warehouseGroupId"));
            if (!user.getWarehouseGroupIds().isEmpty()) {
                predicates.add(criteriaBuilder.or(p2, criteriaBuilder.in(root.get("warehouseGroupId")).value(user.getWarehouseGroupIds())));
            } else {
                predicates.add(p2);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(stockVo.getPageIndex() - 1, stockVo.getPageSize()));
        return BaseJson.getSuccess(page);
    }

    /**
     * 查询总计数量
     *
     * @param stockVo
     * @param request
     * @return
     */
    @GetMapping(value = "stock_total")
    public BaseJson stockTotal(StockVo stockVo, HttpServletRequest request) {
        UserSessionBo user = loginService.getUserSession(request);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Integer> query = builder.createQuery(Integer.class);
        Root<StockWarehousePo> root = query.from(StockWarehousePo.class);
        query.select(builder.sum(root.get("stockCount")));
        query.where((new Specification<StockWarehousePo>() {
            @Override
            public Predicate toPredicate(Root<StockWarehousePo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.notEqual(root.get("stockCount"), 0));
                if (StringUtils.isNotBlank(stockVo.getWarehouseCode())) {
                    predicates.add(criteriaBuilder.equal(root.get("warehouseCode"), stockVo.getWarehouseCode()));
                }
                if (StringUtils.isNotBlank(stockVo.getGoodsCode())) {
                    predicates.add(criteriaBuilder.equal(root.get("goodsCode"), stockVo.getGoodsCode()));
                }
                if (StringUtils.isNotBlank(stockVo.getGoodsColorCode())) {
                    predicates.add(criteriaBuilder.equal(root.get("goodsColorCode"), stockVo.getGoodsColorCode()));
                }
                if (StringUtils.isNotBlank(stockVo.getGoodsColorName())) {
                    predicates.add(criteriaBuilder.equal(root.get("goodsColorName"), stockVo.getGoodsColorName()));
                }
                if (StringUtils.isNotBlank(stockVo.getGoodsSizeName())) {
                    predicates.add(criteriaBuilder.equal(root.get("goodsSizeName"), stockVo.getGoodsSizeName()));
                }
                if (StringUtils.isNotBlank(stockVo.getGoodsSizeName())) {
                    predicates.add(criteriaBuilder.equal(root.get("goodsSizeName"), stockVo.getGoodsSizeName()));
                }
                Predicate p1 = criteriaBuilder.isNull(root.get("goodsGroupId"));
                if (!user.getGoodsGroupIds().isEmpty()) {
                    predicates.add(criteriaBuilder.or(p1, criteriaBuilder.in(root.get("goodsGroupId")).value(user.getGoodsGroupIds())));
                } else {
                    predicates.add(p1);
                }
                Predicate p2 = criteriaBuilder.isNull(root.get("warehouseGroupId"));
                if (!user.getWarehouseGroupIds().isEmpty()) {
                    predicates.add(criteriaBuilder.or(p2, criteriaBuilder.in(root.get("warehouseGroupId")).value(user.getWarehouseGroupIds())));
                } else {
                    predicates.add(p2);
                }
                return predicate;
            }
        }).toPredicate(root, query, builder));
        Integer i = em.createQuery(query).getSingleResult();
        i = i == null ? 0 : i;
        return BaseJson.getSuccess(i);
    }


}
