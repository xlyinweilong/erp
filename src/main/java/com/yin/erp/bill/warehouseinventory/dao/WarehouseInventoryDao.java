package com.yin.erp.bill.warehouseinventory.dao;


import com.yin.erp.bill.common.dao.BaseBillInventoryDao;
import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.warehouseinventory.entity.po.WarehouseInventoryPo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;


/**
 * 仓库损益
 *
 * @author yin
 */
@Resource
public interface WarehouseInventoryDao extends BaseBillInventoryDao<WarehouseInventoryPo, String>,BaseBillWarehouseDao<WarehouseInventoryPo, String> {

    @Query("select DISTINCT t.billDate from WarehouseInventoryPo t where t.warehouseId = :warehouseId and t.status = 'AUDITED' order by t.billDate desc")
    List<LocalDate> findBillDate4Pd(@Param("warehouseId") String warehouseId);

    @Query("select t from WarehouseInventoryPo t where t.warehouseId = :warehouseId AND t.billDate = :billDate and t.status = 'AUDITED'")
    List<BillPo> findAll4Pd(@Param("warehouseId") String warehouseId, @Param("billDate") LocalDate billDate);

    @Query("select t from WarehouseInventoryPo t where t.parentBillId = :parentBillId")
    List<WarehouseInventoryPo> findAllByParentBillId(@Param("parentBillId") String parentBillId);

}
