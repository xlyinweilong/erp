package com.yin.erp.bill.channelinventory.dao;


import com.yin.erp.bill.channelinventory.entity.po.ChannelInventoryPo;
import com.yin.erp.bill.common.dao.BaseBillInventoryDao;
import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import com.yin.erp.bill.common.entity.po.BillPo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;


/**
 * 店铺损益
 *
 * @author yin
 */
@Resource
public interface ChannelInventoryDao extends BaseBillInventoryDao<ChannelInventoryPo, String>,BaseBillChannelDao<ChannelInventoryPo, String> {

    @Query("select DISTINCT t.billDate from ChannelInventoryPo t where t.channelId = :channelId and t.status = 'AUDITED' order by t.billDate desc")
    List<LocalDate> findBillDate4Pd(@Param("channelId") String channelId);

    @Query("select t from ChannelInventoryPo t where t.channelId = :channelId AND t.billDate = :billDate and t.status = 'AUDITED'")
    List<BillPo> findAll4Pd(@Param("channelId") String channelId, @Param("billDate") LocalDate billDate);

    @Query("select t from ChannelInventoryPo t where t.parentBillId = :parentBillId")
    List<ChannelInventoryPo> findAllByParentBillId(@Param("parentBillId") String parentBillId);
}
