package com.yin.erp.bill.channelloss.dao;


import com.yin.erp.bill.channelloss.entity.po.ChannelLossPo;
import com.yin.erp.bill.common.dao.channel.BaseBillChannelDao;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;


/**
 * 店铺损益
 *
 * @author yin
 */
@Resource
public interface ChannelLossDao extends BaseBillChannelDao<ChannelLossPo, String> {
    
    @Query("SELECT t from ChannelLossPo t where t.parentBillId = :parentBillId")
    List<ChannelLossPo> findAllByParentBillId(@Param("parentBillId") String parentId);
}
