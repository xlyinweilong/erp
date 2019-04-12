package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.PosCashCouponPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 销售使用的待用卷
 *
 * @author yin
 */
@Repository
public interface PosCashCouponDao extends JpaRepository<PosCashCouponPo, String>, JpaSpecificationExecutor {


}
