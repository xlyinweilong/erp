package com.yin.erp.bill.order.dao;

import com.yin.erp.bill.common.dao.BaseBillGoodsDao;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.order.entity.po.OrderGoodsPo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订货单货品
 *
 * @author yin
 */
@Resource
public interface OrderGoodsDao extends BaseBillGoodsDao<OrderGoodsPo, String> {

    @Modifying
    @Query("delete from OrderGoodsPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Query("select t from OrderGoodsPo t where t.billId = :billId order by t.billOrder asc")
    @Override
    List<BillGoodsPo> findByBillId(@Param("billId") String billId);

}