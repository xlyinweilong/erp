package com.yin.erp.bill.warehouse2supplier.dao;

import com.yin.erp.bill.common.dao.BaseBillGoodsDao;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.warehouse2supplier.entity.po.Warehouse2SupplierGoodsPo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单据货品
 *
 * @author yin
 */
@Resource
public interface Warehouse2SupplierGoodsDao extends BaseBillGoodsDao<Warehouse2SupplierGoodsPo, String> {

    @Modifying
    @Query("delete from Warehouse2SupplierGoodsPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Query("select t from Warehouse2SupplierGoodsPo t where t.billId = :billId order by t.billOrder asc")
    @Override
    List<BillGoodsPo> findByBillId(@Param("billId") String billId);

}