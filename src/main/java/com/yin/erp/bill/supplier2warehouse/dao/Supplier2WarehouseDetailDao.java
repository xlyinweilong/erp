package com.yin.erp.bill.supplier2warehouse.dao;

import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehouseDetailPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单据详情
 *
 * @author yin
 */
@Resource
public interface Supplier2WarehouseDetailDao  extends PagingAndSortingRepository<Supplier2WarehouseDetailPo, String>, JpaSpecificationExecutor {

    @Modifying
    @Query("delete from Supplier2WarehouseDetailPo t where t.billId = :billId")
    int deleteAllByBillId(@Param("billId") String billId);

    List<Supplier2WarehouseDetailPo> findByBillId(String billId);
}
