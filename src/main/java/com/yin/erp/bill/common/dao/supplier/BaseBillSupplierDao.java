package com.yin.erp.bill.common.dao.supplier;

import com.yin.erp.bill.common.dao.BaseBillDao;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * 单据DAO
 *
 * @author yin.weilong
 * @date 2018.12.25
 */
@NoRepositoryBean
public interface BaseBillSupplierDao<T, String> extends BaseBillDao<T, String>, JpaSpecificationExecutor {

    Long countBySupplierId(String supplierId);
}
