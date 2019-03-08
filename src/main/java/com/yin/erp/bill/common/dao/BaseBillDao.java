package com.yin.erp.bill.common.dao;

import com.yin.erp.bill.common.entity.po.BillPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 单据DAO
 *
 * @author yin.weilong
 * @date 2018.12.25
 */
@NoRepositoryBean
public interface BaseBillDao<T, String> extends JpaRepository<T, String>, JpaSpecificationExecutor {

    BillPo findByCode(String code);

}
