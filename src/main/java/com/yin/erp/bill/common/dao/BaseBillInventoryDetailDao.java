package com.yin.erp.bill.common.dao;

import com.yin.erp.bill.common.entity.po.BillDetailPo;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 单据盘点DAO
 *
 * @author yin.weilong
 * @date 2018.12.25
 */
@NoRepositoryBean
public interface BaseBillInventoryDetailDao<T, String> extends BaseBillDetailDao<T, String> {

    List<BillDetailPo> findInBillIds(@Param("billIdList") List<java.lang.String> billIdList);

}
