package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.PosCashDetailPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 销售货品明细
 *
 * @author yin
 */
@Repository
public interface PosCashDetailDao extends JpaRepository<PosCashDetailPo, String>, JpaSpecificationExecutor {

    /**
     * 查询某个单据的货品
     *
     * @param billId
     * @return
     */
    List<PosCashDetailPo> findAllByBillId(String billId);
}
