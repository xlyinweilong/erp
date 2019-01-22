package com.yin.erp.bill.common.dao;

import com.yin.erp.bill.common.entity.po.BillDetailPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * 单据详情DAO
 *
 * @author yin.weilong
 * @date 2018.12.25
 */
@NoRepositoryBean
public interface BaseBillDetailDao<T, String> extends JpaRepository<T, String>, JpaSpecificationExecutor {

    /**
     * 通过单据ID删除
     *
     * @param billId
     * @return
     */
    int deleteAllByBillId(String billId);

    /**
     * 通过单据ID查询
     *
     * @param billId
     * @return
     */
    List<BillDetailPo> findByBillId(String billId);
}
