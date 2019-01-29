package com.yin.erp.vip.xp.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.vip.grade.dao.VipGradeDao;
import com.yin.erp.vip.xp.dao.VipXpUpRuleDao;
import com.yin.erp.vip.xp.dao.VipXpUpRuleGoodsDao;
import com.yin.erp.vip.xp.entity.po.VipXpUpRuleGoodsPo;
import com.yin.erp.vip.xp.entity.po.VipXpUpRulePo;
import com.yin.erp.vip.xp.entity.vo.VipXpUpRuleGoodsVo;
import com.yin.erp.vip.xp.entity.vo.VipXpUpRuleVo;
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
 * 会员积分增加规则
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/vip/xp_up_rule")
@Transactional(rollbackFor = Throwable.class)
public class VipXpUpRuleController {

    @Autowired
    private VipGradeDao vipGradeDao;
    @Autowired
    private VipXpUpRuleDao vipXpUpRuleDao;
    @Autowired
    private VipXpUpRuleGoodsDao vipXpUpRuleGoodsDao;
    @Autowired
    private DictDao dictDao;


    /**
     * 保存
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody VipXpUpRuleVo vo) throws MessageException {
        VipXpUpRulePo po = new VipXpUpRulePo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = vipXpUpRuleDao.findById(vo.getId()).get();
        }
        po.setName(vo.getName());
        po.setEndDate(vo.getEndDate());
        po.setStartDate(vo.getStartDate());
        po.setXp(vo.getXp());
        po.setPriority(vo.getPriority());
        po.setVipGradeId(vo.getVipGradeId());
        vipXpUpRuleDao.save(po);
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
    public BaseJson list(VipXpUpRuleVo vo) throws MessageException {
        Page<VipXpUpRulePo> page = vipXpUpRuleDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(vo.getSearchKey())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%"));
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
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            vipXpUpRuleDao.deleteById(id);
        }
        return BaseJson.getSuccess();
    }


    /**
     * 货品列表
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "goods_list")
    public BaseJson goodsList(VipXpUpRuleGoodsVo vo) throws MessageException {
        Page<VipXpUpRuleGoodsPo> page = vipXpUpRuleGoodsDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("vipXpUpRuleId"), vo.getVipXpUpRuleId()));
            if (StringUtils.isNotBlank(vo.getSearchKey())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        return BaseJson.getSuccess(page);
    }

    /**
     * 设置等级上下关系
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save_goods")
    public BaseJson saveGoods(@RequestBody VipXpUpRuleGoodsVo vo) throws MessageException {
        VipXpUpRuleGoodsPo po = new VipXpUpRuleGoodsPo();
        if(StringUtils.isNotBlank(vo.getId())){
            VipXpUpRuleGoodsPo dbPo = vipXpUpRuleGoodsDao.findById(vo.getId()).get();
            po.setVersion(dbPo.getVersion());
            po.setCreateDate(dbPo.getCreateDate());
        }
        if (StringUtils.isNotBlank(vo.getGoodsBrandId())) {
            po.setGoodsBrandId(vo.getGoodsBrandId());
            po.setGoodsBrandName(dictDao.findById(vo.getGoodsBrandId()).get().getName());
        }
        if (StringUtils.isNotBlank(vo.getGoodsCategoryId())) {
            po.setGoodsCategoryId(vo.getGoodsCategoryId());
            po.setGoodsCategoryName(dictDao.findById(vo.getGoodsCategoryId()).get().getName());
        }
        po.setGoodsId(vo.getGoodsId());
        if (StringUtils.isNotBlank(vo.getGoodsSeasonId())) {
            po.setGoodsSeasonId(vo.getGoodsSeasonId());
            po.setGoodsSeasonName(dictDao.findById(vo.getGoodsSeasonId()).get().getName());
        }

        if (StringUtils.isNotBlank(vo.getGoodsYearId())) {
            po.setGoodsYearId(vo.getGoodsYearId());
            po.setGoodsYearName(dictDao.findById(vo.getGoodsYearId()).get().getName());
        }
//            po.setXp(ruleGoodsVo.getXp());
//            po.setName(ruleGoodsVo.getName());
//            po.setPriority(ruleGoodsVo.getPriority());
//            po.setStartDate(ruleGoodsVo.getStartDate());
//            po.setEndDate(ruleGoodsVo.getEndDate());
//            po.setVipGradeId(ruleGoodsVo.getVipGradeId());
        po.setVipXpUpRuleId(vo.getVipXpUpRuleId());
        vipXpUpRuleGoodsDao.save(po);
        return BaseJson.getSuccess();
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "delete_goods")
    public BaseJson deleteGoods(@RequestBody BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            vipXpUpRuleGoodsDao.deleteById(id);
        }
        return BaseJson.getSuccess();
    }

}
