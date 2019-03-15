package com.yin.erp.info.supplier.service;

import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.bill.common.dao.supplier.BaseBillSupplierDao;
import com.yin.erp.info.dict.feign.DictFeign;
import com.yin.erp.info.supplier.dao.SupplierDao;
import com.yin.erp.info.supplier.entity.po.SupplierPo;
import com.yin.erp.info.supplier.entity.vo.SupplierVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 供应商服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class SupplierService {

    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private DictFeign dictFeign;
    @Autowired
    private ApplicationContext context;

    /**
     * 保存
     *
     * @param vo
     * @throws Exception
     */
    public void save(SupplierVo vo) throws MessageException {
        SupplierPo po = new SupplierPo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = supplierDao.findById(vo.getId()).get();
            //发送给队列，全局做数据更新 TODO
        }
        po.setCode(vo.getCode());
        po.setName(vo.getName());
        po.setGroupId(vo.getGroupId());
        po.setGroupName(dictFeign.getNameById(vo.getGroupId()));
        supplierDao.save(po);
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public SupplierVo findById(String id) {
        SupplierPo dictPo = supplierDao.findById(id).get();
        SupplierVo dictVo = new SupplierVo();
        dictVo.setId(dictPo.getId());
        dictVo.setCode(dictPo.getCode());
        dictVo.setName(dictPo.getName());
        dictVo.setGroupId(dictPo.getGroupId());
        dictVo.setGroupName(dictPo.getGroupName());
        return dictVo;
    }

    /**
     * 查询字典
     *
     * @param vo
     * @return
     */
    public Page<SupplierPo> findSupplierPage(SupplierVo vo, UserSessionBo user) {
        Page<SupplierPo> page = supplierDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSearchKey())) {
                Predicate p1 = criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%");
                Predicate p2 = criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%");
                Predicate predicatesPermission = criteriaBuilder.or(p1, p2);
                predicates.add(predicatesPermission);
            }
            Predicate p1 = criteriaBuilder.isNull(root.get("groupId"));
            if (!user.getSupplierGroupIds().isEmpty()) {
                predicates.add(criteriaBuilder.or(p1, criteriaBuilder.in(root.get("groupId")).value(user.getSupplierGroupIds())));
            } else {
                predicates.add(p1);
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
    public void delete(BaseDeleteVo vo) throws MessageException{
        for (String id : vo.getIds()) {
            //单据引用
            Map<String, BaseBillSupplierDao> beans = context.getBeansOfType(BaseBillSupplierDao.class);
            for (String beanName : beans.keySet()) {
                if (beans.get(beanName).countBySupplierId(id) > 0L) {
                    throw new MessageException("数据已经被引用，无法删除");
                }
            }
            supplierDao.deleteById(id);
        }
    }


}
