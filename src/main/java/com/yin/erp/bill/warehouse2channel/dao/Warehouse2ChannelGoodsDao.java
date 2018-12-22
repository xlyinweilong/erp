package com.yin.erp.bill.warehouse2channel.dao;

import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelGoodsPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 单据货品
 *
 * @author yin
 */
@Resource
public interface Warehouse2ChannelGoodsDao extends PagingAndSortingRepository<Warehouse2ChannelGoodsPo, String>, JpaSpecificationExecutor {

    @Modifying
    @Query("delete from Warehouse2ChannelGoodsPo t where t.billId = :billId")
    int deleteAllByBillId(String billId);

    @Query("select t from Warehouse2ChannelGoodsPo t where t.billId = :billId order by t.id desc")
    List<Warehouse2ChannelGoodsPo> findByBillId(@Param("billId") String billId);

}