package com.yin.erp.pos.cash.service;

import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.pos.cash.dao.PosCashDetailDao;
import com.yin.erp.pos.cash.entity.po.PosCashDetailPo;
import com.yin.erp.pos.cash.entity.vo.in.PosSearchVo;
import com.yin.erp.pos.cash.entity.vo.out.PosSearchOutTotalVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 销售服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class CashSearchService {

    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private PosCashDetailDao posCashDetailDao;
    @Autowired
    private EntityManager em;


    /**
     * 查询销售
     *
     * @param posSearchVo
     * @param user
     */
    public Page<PosCashDetailPo> posList(PosSearchVo posSearchVo, UserSessionBo user) throws MessageException {
        Page<PosCashDetailPo> page = posCashDetailDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            return criteriaBuilder.and(this.searchCondition(criteriaBuilder, root, posSearchVo, user));
        }, PageRequest.of(0, 10, Sort.Direction.DESC, "createDate"));
        for (PosCashDetailPo posCashDetailPo : page.getContent()) {
            posCashDetailPo.setChannelCode(posCashDetailPo.getChannelPo().getCode());
            posCashDetailPo.setChannelName(posCashDetailPo.getChannelPo().getName());
        }
        return page;
    }


    /**
     * 查询销售总计
     *
     * @param posSearchVo
     * @param user
     * @return
     */
    public PosSearchOutTotalVo posTotal(PosSearchVo posSearchVo, UserSessionBo user) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<PosSearchOutTotalVo> query = builder.createQuery(PosSearchOutTotalVo.class);
        Root<PosCashDetailPo> root = query.from(PosCashDetailPo.class);
        Path<BigDecimal> amountPath = root.get("amount");
        Path<Integer> billCountPath = root.get("billCount");
        query.multiselect(builder.sum(amountPath).as(BigDecimal.class), builder.sum(billCountPath));
        query.where(this.searchCondition(builder, root, posSearchVo, user));
        PosSearchOutTotalVo vo = em.createQuery(query).getSingleResult();
        return vo;
    }

    /**
     * 查询条件
     *
     * @param builder
     * @param root
     * @param posSearchVo
     * @param user
     * @return
     */
    private Predicate[] searchCondition(CriteriaBuilder builder, Root root, PosSearchVo posSearchVo, UserSessionBo user) {
        ZoneId zone = ZoneId.systemDefault();
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(builder.lessThanOrEqualTo(root.get("billDate"), Date.from(posSearchVo.getEndDate().plusDays(1L).atStartOfDay().atZone(zone).toInstant())));
        predicateList.add(builder.greaterThanOrEqualTo(root.get("billDate"), Date.from(posSearchVo.getStartDate().atStartOfDay().atZone(zone).toInstant())));
        predicateList.add(builder.equal(root.get("status"), "AUDITED"));

        //渠道
        if (StringUtils.isNotBlank(posSearchVo.getChannelCode())) {
            ChannelPo channelPo = channelDao.findByCode(posSearchVo.getChannelCode());
            if (channelPo != null) {
                predicateList.add(builder.equal(root.get("channelId"), channelPo.getId()));
            } else {
                predicateList.add(builder.isNull(root.get("channelId")));
            }
        }
        if (StringUtils.isNotBlank(posSearchVo.getGoodsCode())) {
            predicateList.add(builder.equal(root.get("goodsCode"), posSearchVo.getGoodsCode()));
        }
        if (StringUtils.isNotBlank(posSearchVo.getGoodsColorCode())) {
            predicateList.add(builder.equal(root.get("goodsColorCode"), posSearchVo.getGoodsColorCode()));
        }
        if (StringUtils.isNotBlank(posSearchVo.getGoodsSizeName())) {
            predicateList.add(builder.equal(root.get("goodsSizeName"), posSearchVo.getGoodsSizeName()));
        }
        //渠道权限
        Join join = root.join("channelPo",JoinType.LEFT);
        Predicate p1 = builder.isNull(join.get("groupId"));
        if (!user.getChannelGroupIds().isEmpty()) {
            Predicate predicate = builder.in(join.get("groupId")).value(user.getChannelGroupIds());
            predicateList.add(builder.or(p1,predicate));
        } else {
            predicateList.add(p1);
        }
        Predicate[] predicates = new Predicate[predicateList.size()];
        predicates = predicateList.toArray(predicates);
        return predicates;
    }

}
