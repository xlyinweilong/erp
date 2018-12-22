package com.yin.erp.bill.warehouse2channel.dao;

import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelDetailPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单据详情
 *
 * @author yin
 */
@Resource
public interface Warehouse2ChannelDetailDao extends PagingAndSortingRepository<Warehouse2ChannelDetailPo, String>, JpaSpecificationExecutor {

    @Modifying
    @Query("delete from Warehouse2ChannelDetailPo t where t.billId = :billId")
    int deleteAllByBillId(String billId);

    List<Warehouse2ChannelDetailPo> findByBillId(String billId);
}
