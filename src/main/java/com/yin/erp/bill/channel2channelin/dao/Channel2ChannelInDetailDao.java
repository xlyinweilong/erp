package com.yin.erp.bill.channel2channelin.dao;

import com.yin.erp.bill.channel2channelin.entity.po.Channel2ChannelInDetailPo;
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
public interface Channel2ChannelInDetailDao extends BaseBillDetailDao<Channel2ChannelInDetailPo, String> {

    @Modifying
    @Query("delete from Channel2ChannelInDetailPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Override
    List<BillDetailPo> findByBillId(String billId);
}
