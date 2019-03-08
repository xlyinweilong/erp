package com.yin.erp.bill.noticechannel2channelout.dao;

import com.yin.erp.bill.common.dao.BaseBillGoodsDao;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.noticechannel2channelout.entity.po.NoticeChannel2ChannelOutGoodsPo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单据货品
 *
 * @author yin
 */
@Resource
public interface NoticeChannel2ChannelOutGoodsDao extends BaseBillGoodsDao<NoticeChannel2ChannelOutGoodsPo, String> {

    @Modifying
    @Query("delete from NoticeChannel2ChannelOutGoodsPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Query("select t from NoticeChannel2ChannelOutGoodsPo t where t.billId = :billId order by t.billOrder asc")
    @Override
    List<BillGoodsPo> findByBillId(@Param("billId") String billId);

}