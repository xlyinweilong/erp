package com.yin.erp.vip.integral.dao;


import com.yin.erp.vip.integral.entity.po.VipIntegralLogPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 会员积分日志
 *
 * @author yin
 */
@Repository
public interface VipIntegralLogDao extends JpaRepository<VipIntegralLogPo, String>, JpaSpecificationExecutor {

}
