package com.yin.erp.vip.xp.dao;


import com.yin.erp.vip.xp.entity.po.VipXpLogPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 会员积分日志
 *
 * @author yin
 */
@Repository
public interface VipXpLogDao extends JpaRepository<VipXpLogPo, String>, JpaSpecificationExecutor {

}
