package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.PosCashDetailPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("select t from PosCashDetailPo t where t.billId = :billId")
    List<PosCashDetailPo> findAllByBillId(@Param("billId") String billId);

    /**
     * 查询单据的退货
     *
     * @param billId
     * @return
     */
    @Query("select t from PosCashDetailPo t where t.billId = :billId and t.billCount <> t.backCount")
    List<PosCashDetailPo> findBackByBillId(@Param("billId") String billId);


    Long countByGoodsId(String goodsId);


    Long countByEmployId(String employId);
}
