package com.yin.erp.bill.channel2channelout.dao;

import com.yin.erp.bill.channel2channelout.entity.po.Channel2ChannelOutGoodsPo;
import com.yin.erp.bill.common.dao.BaseBillGoodsDao;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
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
public interface Channel2ChannelOutGoodsDao extends BaseBillGoodsDao<Channel2ChannelOutGoodsPo, String> {

    @Modifying
    @Query("delete from Channel2ChannelOutGoodsPo t where t.billId = :billId")
    @Override
    int deleteAllByBillId(@Param("billId") String billId);

    @Query("select t from Channel2ChannelOutGoodsPo t where t.billId = :billId order by t.id desc")
    @Override
    List<BillGoodsPo> findByBillId(@Param("billId") String billId);

}