package com.yin.erp.bill.supplier2channel.dao;

import com.yin.erp.bill.common.dao.BaseBillDetailDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.supplier2channel.entity.po.Supplier2ChannelDetailPo;
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
public interface Supplier2ChannelDetailDao extends BaseBillDetailDao<Supplier2ChannelDetailPo, String> {

    @Modifying
    @Query("delete from Supplier2ChannelDetailPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Override
    List<BillDetailPo> findByBillId(String billId);
}
