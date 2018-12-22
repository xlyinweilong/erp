package com.yin.erp.info.employ.dao;


import com.yin.erp.info.employ.entity.po.EmployPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Resource;

/**
 * 营业员
 *
 * @author yin
 */
@Resource
public interface EmployDao extends PagingAndSortingRepository<EmployPo, String>, JpaSpecificationExecutor {

}
