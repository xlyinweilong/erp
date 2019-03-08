package com.yin.erp.bill.warehouseloss.dao;


import com.yin.erp.bill.common.dao.warehouse.BaseBillWarehouseDao;
import com.yin.erp.bill.warehouseloss.entity.po.WarehouseLossPo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;


/**
 * 店铺损益
 *
 * @author yin
 */
@Resource
public interface WarehouseLossDao extends BaseBillWarehouseDao<WarehouseLossPo, String> {

    @Query("SELECT t from WarehouseLossPo t where t.parentBillId = :parentBillId")
    List<WarehouseLossPo> findAllByParentBillId(@Param("parentBillId") String parentId);

}
