package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.CashGoodsPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 销售货品明细
 * @author yin
 */
@Repository
public interface CashGoodsDao extends JpaRepository<CashGoodsPo, String>, JpaSpecificationExecutor {

}
