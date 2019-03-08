package com.yin.erp.bill.purchase.dao;

import com.yin.erp.bill.common.dao.BaseBillDetailDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.purchase.entity.po.PurchaseDetailPo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 采购单详情
 *
 * @author yin
 */
@Resource
public interface PurchaseDetailDao extends BaseBillDetailDao<PurchaseDetailPo, String> {

    @Modifying
    @Query("delete from PurchaseDetailPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Override
    List<BillDetailPo> findByBillId(String billId);
}
