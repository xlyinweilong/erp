package com.yin.erp.bill.channelinventory.dao;

import com.yin.erp.bill.channelinventory.entity.po.ChannelInventoryDetailPo;
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
public interface ChannelInventoryDetailDao extends BaseBillDetailDao<ChannelInventoryDetailPo, String> {

    @Modifying
    @Query("delete from ChannelInventoryDetailPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Override
    List<BillDetailPo> findByBillId(String billId);
}
