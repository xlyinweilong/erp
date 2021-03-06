package com.yin.erp.bill.warehouseinventory.dao;

import com.yin.erp.bill.common.dao.BaseBillInventoryDetailDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.warehouseinventory.entity.po.WarehouseInventoryDetailPo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单据详情
 *
 * @author yin
 */
@Resource
public interface WarehouseInventoryDetailDao extends BaseBillInventoryDetailDao<WarehouseInventoryDetailPo, String> {

    @Modifying
    @Query("delete from WarehouseInventoryDetailPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Override
    List<BillDetailPo> findByBillId(String billId);

    @Override
    @Query("select t from WarehouseInventoryDetailPo t where t.billId in :billIdList")
    List<BillDetailPo> findInBillIds(@Param("billIdList") List<String> billIdList);
}
