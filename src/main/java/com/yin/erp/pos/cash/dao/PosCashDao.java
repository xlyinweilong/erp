package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.PosCashPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 销售
 *
 * @author yin
 */
@Repository
public interface PosCashDao extends JpaRepository<PosCashPo, String>, JpaSpecificationExecutor {

    /**
     * 根据单号查询
     *
     * @param code
     * @return
     */
    PosCashPo findByCode(String code);

}
