package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.PosCashEmployPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 销售营业员
 *
 * @author yin
 */
@Repository
public interface PosCashEmployDao extends JpaRepository<PosCashEmployPo, String>, JpaSpecificationExecutor {


    Long countByEmployId(String employId);
}
