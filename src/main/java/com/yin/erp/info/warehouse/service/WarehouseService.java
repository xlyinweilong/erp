package com.yin.erp.info.warehouse.service;

import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.config.sysconfig.dao.ConfigWarehouseDao;
import com.yin.erp.config.sysconfig.entity.po.ConfigPo;
import com.yin.erp.config.sysconfig.entity.po.ConfigWarehousePo;
import com.yin.erp.info.dict.feign.DictFeign;
import com.yin.erp.info.warehouse.dao.WarehouseDao;
import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import com.yin.erp.info.warehouse.entity.vo.WarehouseVo;
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
 * 仓库服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class WarehouseService {

    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private DictFeign dictFeign;
    @Autowired
    private ConfigWarehouseDao configWarehouseDao;
    @Autowired
    private ApplicationContext context;

    /**
     * 保存
     *
     * @param vo
     * @throws Exception
     */
    public void save(WarehouseVo vo) throws MessageException {
        WarehousePo po = new WarehousePo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = warehouseDao.findById(vo.getId()).get();
            //发送给队列，全局做数据更新 TODO
        }
        po.setCode(vo.getCode());
        po.setName(vo.getName());
        po.setGroupId(vo.getGroupId());
        po.setGroupName(dictFeign.getNameById(vo.getGroupId()));
        warehouseDao.save(po);
        configWarehouseDao.deleteAllByWarehouseId(po.getId());
        for (ConfigPo configPo : vo.getWarehouseConfigList()) {
            configWarehouseDao.save(new ConfigWarehousePo(configPo.getId(), po.getId(), configPo.getDefaultValue()));
        }
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public WarehouseVo findById(String id) {
        WarehousePo po = warehouseDao.findById(id).get();
        WarehouseVo vo = new WarehouseVo();
        vo.setId(po.getId());
        vo.setCode(po.getCode());
        vo.setName(po.getName());
        vo.setGroupId(po.getGroupId());
        vo.setGroupName(po.getGroupName());
        vo.setWarehouseConfigWarehouseList(configWarehouseDao.findByWarehouseId(id));
        return vo;
    }

    /**
     * 查询字典
     *
     * @param vo
     * @return
     */
    public Page<WarehousePo> findWarehousePage(WarehouseVo vo, UserSessionBo user) {
        Page<WarehousePo> page = warehouseDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getName() + "%"));
            }
            Predicate p1 = criteriaBuilder.isNull(root.get("groupId"));
            if (!user.getWarehouseGroupIds().isEmpty()) {
                predicates.add(criteriaBuilder.or(p1, criteriaBuilder.in(root.get("groupId")).value(user.getWarehouseGroupIds())));
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
            Map<String, BaseBillWarehouseDao> beans = context.getBeansOfType(BaseBillWarehouseDao.class);
            for (String beanName : beans.keySet()) {
                if (beans.get(beanName).countByWarehouseId(id) > 0L) {
                    throw new MessageException("数据已经被引用，无法删除");
                }
            }
            warehouseDao.deleteById(id);
            configWarehouseDao.deleteAllByWarehouseId(id);
        }
    }


}
