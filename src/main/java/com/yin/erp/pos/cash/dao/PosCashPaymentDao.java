package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.PosCashPaymentPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 销售支付方式
 *
 * @author yin
 */
@org.springframework.transaction.annotation.Transactional(rollbackFor = Throwable.class)
@Repository
public interface PosCashPaymentDao extends JpaRepository<PosCashPaymentPo, String>, JpaSpecificationExecutor {

    /**
     * 查询某个单据的支付方式
     *
     * @param billId
     * @return
     */
    List<PosCashPaymentPo> findAllByBillId(String billId);

    /**
     * 查询数量
     *
     * @param paymentId
     * @return
     */
    Long countByPaymentId(String paymentId);
}
