package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.CashPaymentPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 销售支付方式
 *
 * @author yin
 */
@org.springframework.transaction.annotation.Transactional(rollbackFor = Throwable.class)
@Repository
public interface CashPaymentDao extends JpaRepository<CashPaymentPo, String>, JpaSpecificationExecutor {


}
