package com.yin.erp.bill.channelinventory.dao;

import com.yin.erp.bill.channelinventory.entity.po.ChannelInventoryDetailPo;
import com.yin.erp.bill.common.dao.BaseBillInventoryDetailDao;
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
public interface ChannelInventoryDetailDao extends BaseBillInventoryDetailDao<ChannelInventoryDetailPo, String> {

    @Modifying
    @Query("delete from ChannelInventoryDetailPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Override
    List<BillDetailPo> findByBillId(String billId);

    @Override
    @Query("select t from ChannelInventoryDetailPo t where t.billId in :billIdList")
    List<BillDetailPo> findInBillIds(@Param("billIdList") List<String> billIdList);
}
