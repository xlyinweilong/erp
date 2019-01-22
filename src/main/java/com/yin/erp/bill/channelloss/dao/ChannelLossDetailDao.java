package com.yin.erp.bill.channelloss.dao;

import com.yin.erp.bill.channelloss.entity.po.ChannelLossDetailPo;
import com.yin.erp.bill.common.dao.BaseBillDetailDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
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
public interface ChannelLossDetailDao extends BaseBillDetailDao<ChannelLossDetailPo, String> {

    @Modifying
    @Query("delete from ChannelLossDetailPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Override
    List<BillDetailPo> findByBillId(String billId);
}
