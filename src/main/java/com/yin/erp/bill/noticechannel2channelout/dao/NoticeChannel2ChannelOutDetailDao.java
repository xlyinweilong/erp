package com.yin.erp.bill.noticechannel2channelout.dao;

import com.yin.erp.bill.common.dao.BaseBillDetailDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.noticechannel2channelout.entity.po.NoticeChannel2ChannelOutDetailPo;
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
public interface NoticeChannel2ChannelOutDetailDao extends BaseBillDetailDao<NoticeChannel2ChannelOutDetailPo, String> {

    @Modifying
    @Query("delete from NoticeChannel2ChannelOutDetailPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Override
    List<BillDetailPo> findByBillId(String billId);
}
