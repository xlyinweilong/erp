package com.yin.erp.vip.grade.service;

import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.vip.grade.dao.VipGradeDao;
import com.yin.erp.vip.grade.entity.po.VipGradePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 会员等级服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class VipGradeService {

    @Autowired
    private VipGradeDao vipGradeDao;


    /**
     * 获取所有的有效的等级
     *
     * @return
     */
    List<VipGradePo> findAllValidGrade() {
        return vipGradeDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNotNull(root.get("indexDepth")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, new Sort(Sort.Direction.ASC, "indexDepth").and(new Sort(Sort.Direction.DESC, "createDate")));
    }

    /**
     * 根据一个会员经验获取该会员的等级
     *
     * @param xp
     * @return
     */
    public VipGradePo getGradeByVip(Integer xp) throws MessageException {
        List<VipGradePo> gradeList = this.findAllValidGrade();
        Optional<VipGradePo> optional = gradeList.stream().sorted((a, b) -> b.getIndexDepth().compareTo(a.getIndexDepth())).filter(g -> g.getLowestXpValue() <= xp).findFirst();
        if (!optional.isPresent()) {
            throw new MessageException("会员等级设置错误，无法获取会员等级");
        }
        return optional.get();
    }

}
