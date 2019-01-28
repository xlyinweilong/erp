package com.yin.erp.vip.info.dao;


import com.yin.erp.vip.info.entity.po.VipPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 会员信息
 *
 * @author yin
 */
@Repository
public interface VipDao extends JpaRepository<VipPo, String>, JpaSpecificationExecutor {

}
