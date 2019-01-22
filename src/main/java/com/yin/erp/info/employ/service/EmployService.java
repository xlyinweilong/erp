package com.yin.erp.info.employ.service;

import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.info.employ.dao.EmployDao;
import com.yin.erp.info.employ.entity.po.EmployPo;
import com.yin.erp.info.employ.entity.vo.EmployVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 员工服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class EmployService {

    @Autowired
    private EmployDao employDao;

    /**
     * 保存
     *
     * @param vo
     * @throws Exception
     */
    public void save(EmployVo vo) throws MessageException {
        EmployPo po = new EmployPo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = employDao.findById(vo.getId()).get();
            //发送给队列，全局做数据更新 TODO
        }
        po.setCode(vo.getCode());
        po.setName(vo.getName());
        employDao.save(po);
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public EmployVo findById(String id) {
        EmployPo dictPo = employDao.findById(id).get();
        EmployVo dictVo = new EmployVo();
        dictVo.setId(dictPo.getId());
        dictVo.setCode(dictPo.getCode());
        dictVo.setName(dictPo.getName());
        return dictVo;
    }

    /**
     * 查询字典
     *
     * @param vo
     * @return
     */
    public Page<EmployPo> findDictPage(EmployVo vo) {
        Page<EmployPo> page = employDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSearchKey())) {
                List<Predicate> predicatesSearch = new ArrayList<>();
                predicatesSearch.add(criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%"));
                predicatesSearch.add(criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%"));
                predicates.add(criteriaBuilder.or(predicatesSearch.toArray(new Predicate[predicatesSearch.size()])));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        return page;
    }

    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            //查询货品/渠道引用情况 TODO
            employDao.deleteById(id);
        }
    }


}
