package com.yin.erp.vip.integral.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.vip.grade.dao.VipGradeDao;
import com.yin.erp.vip.grade.entity.po.VipGradePo;
import com.yin.erp.vip.grade.entity.vo.VipGradeVo;
import com.yin.erp.vip.integral.dao.VipIntegralUpRuleDao;
import com.yin.erp.vip.integral.dao.VipIntegralUpRuleGoodsDao;
import com.yin.erp.vip.integral.entity.po.VipIntegralUpRuleGoodsPo;
import com.yin.erp.vip.integral.entity.po.VipIntegralUpRulePo;
import com.yin.erp.vip.integral.entity.vo.VipIntegralUpRuleGoodsVo;
import com.yin.erp.vip.integral.entity.vo.VipIntegralUpRuleVo;
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
@RequestMapping(value = "api/vip/integral_up_rule")
@Transactional(rollbackFor = Throwable.class)
public class VipIntegralUpRuleController {

    @Autowired
    private VipGradeDao vipGradeDao;
    @Autowired
    private VipIntegralUpRuleDao vipIntegralUpRuleDao;
    @Autowired
    private VipIntegralUpRuleGoodsDao vipIntegralUpRuleGoodsDao;
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
    public BaseJson save(@Validated @RequestBody VipIntegralUpRuleVo vo) throws MessageException {
        VipIntegralUpRulePo po = new VipIntegralUpRulePo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = vipIntegralUpRuleDao.findById(vo.getId()).get();
        }
        po.setName(vo.getName());
        po.setEndDate(vo.getEndDate());
        po.setStartDate(vo.getStartDate());
        po.setIntegral(vo.getIntegral());
        po.setPriority(vo.getPriority());
        po.setVipGradeId(vo.getVipGradeId());
        vipIntegralUpRuleDao.save(po);
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
        Page<VipIntegralUpRulePo> page = vipIntegralUpRuleDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
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
            vipIntegralUpRuleDao.deleteById(id);
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
    public BaseJson goodsList(VipIntegralUpRuleGoodsVo vo) throws MessageException {
        Page<VipIntegralUpRuleGoodsPo> page = vipIntegralUpRuleGoodsDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("vipIntegralUpRuleId"), vo.getVipIntegralUpRuleId()));
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
    public BaseJson saveGoods(@RequestBody VipIntegralUpRuleGoodsVo vo) throws MessageException {
        VipIntegralUpRuleGoodsPo po = new VipIntegralUpRuleGoodsPo();
        if(StringUtils.isNotBlank(vo.getId())){
            VipIntegralUpRuleGoodsPo dbPo = vipIntegralUpRuleGoodsDao.findById(vo.getId()).get();
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
//            po.setIntegral(ruleGoodsVo.getIntegral());
//            po.setName(ruleGoodsVo.getName());
//            po.setPriority(ruleGoodsVo.getPriority());
//            po.setStartDate(ruleGoodsVo.getStartDate());
//            po.setEndDate(ruleGoodsVo.getEndDate());
//            po.setVipGradeId(ruleGoodsVo.getVipGradeId());
        po.setVipIntegralUpRuleId(vo.getVipIntegralUpRuleId());
        vipIntegralUpRuleGoodsDao.save(po);
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
            vipIntegralUpRuleGoodsDao.deleteById(id);
        }
        return BaseJson.getSuccess();
    }
    /**
     * 设置升级规则
     *
     * @param list
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "set_grade_up_rule")
    public BaseJson setGradeUpRule(@RequestBody List<VipGradeVo> list) throws MessageException {
        //清除升级规则
        for (VipGradeVo vo : list) {
            VipGradePo po = vipGradeDao.findById(vo.getId()).get();
            po.setLowestXpValue(vo.getLowestXpValue());
            vipGradeDao.save(po);
        }
        return BaseJson.getSuccess();
    }

}
