package com.yin.erp.vip.coupon.dao;


import com.yin.erp.vip.coupon.entity.po.VipCouponPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 优惠卷
 *
 * @author yin
 */
@Repository
public interface VipCouponDao extends JpaRepository<VipCouponPo, String>, JpaSpecificationExecutor {

    VipCouponPo findByCode(String code);

}
