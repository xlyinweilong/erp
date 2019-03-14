package com.yin.erp.info.employ.dao;


import com.yin.erp.info.employ.entity.po.EmployPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;

/**
 * 营业员
 *
 * @author yin
 */
@Resource
public interface EmployDao extends JpaRepository<EmployPo, String>, JpaSpecificationExecutor {

    /**
     * 根据编号查询
     *
     * @param code
     * @return
     */
    EmployPo findByCode(String code);
}
