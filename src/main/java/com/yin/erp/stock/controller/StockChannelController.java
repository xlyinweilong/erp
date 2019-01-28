package com.yin.erp.stock.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.stock.dao.StockChannelDao;
import com.yin.erp.stock.entity.po.StockChannelPo;
import com.yin.erp.stock.entity.vo.StockVo;
import com.yin.erp.stock.service.StockChannelService;
import com.yin.erp.user.user.service.LoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 渠道制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/stock/channel")
public class StockChannelController {

    @Autowired
    private StockChannelService stockChannelService;
    @Autowired
    private StockChannelDao stockChannelDao;
    @Autowired
    private LoginService loginService;


    /**
     * stock_info
     * 渠道库存详情
     *
     * @return
     */
    @GetMapping(value = "stock_info")
    public BaseJson stockInfo(String channelId, String goodsId, String goodsColorId, String goodsSizeId) {
        return BaseJson.getSuccess(stockChannelDao.findByChannelIdAndGoodsIdAndGoodsColorIdAndGoodsSizeId(channelId, goodsId, goodsColorId, goodsSizeId));
    }

    @GetMapping(value = "stock_list")
    public BaseJson stockList(StockVo stockVo, HttpServletRequest request) {
        UserSessionBo user = loginService.getUserSession(request);
        Page<StockChannelPo> page = stockChannelDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(stockVo.getChannelCode())) {
                predicates.add(criteriaBuilder.equal(root.get("channelCode"), stockVo.getChannelCode()));
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
            Predicate p2 = criteriaBuilder.isNull(root.get("channelGroupId"));
            if (!user.getChannelGroupIds().isEmpty()) {
                predicates.add(criteriaBuilder.or(p2, criteriaBuilder.in(root.get("channelGroupId")).value(user.getChannelGroupIds())));
            } else {
                predicates.add(p2);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(stockVo.getPageIndex() - 1, stockVo.getPageSize()));
        return BaseJson.getSuccess(page);
    }


}
