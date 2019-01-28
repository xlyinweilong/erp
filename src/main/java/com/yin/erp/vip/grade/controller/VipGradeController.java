package com.yin.erp.vip.grade.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.vip.grade.dao.VipGradeDao;
import com.yin.erp.vip.grade.entity.po.VipGradePo;
import com.yin.erp.vip.grade.entity.vo.VipGradeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员等级控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/vip/grade")
@Transactional(rollbackFor = Throwable.class)
public class VipGradeController {

    @Autowired
    private VipGradeDao vipGradeDao;


    /**
     * 保存
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody VipGradeVo vo) throws MessageException {
        VipGradePo po = new VipGradePo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = vipGradeDao.findById(vo.getId()).get();
        }
        po.setName(vo.getName());
        po.setIndexDepth(vo.getIndexDepth());
        vipGradeDao.save(po);
        return BaseJson.getSuccess();
    }

    /**
     * 获取
     *
     * @param searchKey
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "list")
    public BaseJson list(String searchKey) throws MessageException {
        List<VipGradePo> list = vipGradeDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(searchKey)) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + searchKey + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, new Sort(Sort.Direction.ASC, "indexDepth").and(new Sort(Sort.Direction.DESC, "createDate")));
        return BaseJson.getSuccess(list);
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "delete")
    public BaseJson save(@RequestBody BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            vipGradeDao.deleteById(id);
        }
        return BaseJson.getSuccess();
    }

    /**
     * 设置等级上下关系
     *
     * @param list
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "set_grade")
    public BaseJson setGrade(@RequestBody List<VipGradeVo> list) throws MessageException {
        //清除所有深度
        vipGradeDao.updateAllIndexDepthNull();
        int i = 1;
        for (VipGradeVo vo : list) {
            VipGradePo po = vipGradeDao.findById(vo.getId()).get();
            po.setDefaultGrade(i == 1);
            po.setIndexDepth(i);
            i++;
            vipGradeDao.save(po);
        }
        return BaseJson.getSuccess();
    }

    /**
     * 设置默认等级
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "set_default_grade")
    public BaseJson setDefaultGrade(@RequestBody VipGradeVo vo) throws MessageException {
        vipGradeDao.updateAllDefaultGradeFalse();
        VipGradePo po = vipGradeDao.findById(vo.getId()).get();
        po.setDefaultGrade(true);
        vipGradeDao.save(po);
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
