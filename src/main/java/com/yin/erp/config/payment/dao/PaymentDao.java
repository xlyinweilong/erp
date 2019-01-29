package com.yin.erp.config.payment.dao;


import com.yin.erp.config.payment.entity.po.PaymentPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 付款方式
 *
 * @author yin
 */
@Repository
public interface PaymentDao extends JpaRepository<PaymentPo, String>, JpaSpecificationExecutor {

}
