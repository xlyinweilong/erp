package com.yin.erp.bill.common.dao;

import com.yin.erp.bill.common.entity.po.BillPo;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 单据盘点DAO
 *
 * @author yin.weilong
 * @date 2018.12.25
 */
@NoRepositoryBean
public interface BaseBillInventoryDao<T, String> extends BaseBillDao<T, String> {

    List<LocalDate> findBillDate4Pd(@Param("channelId") java.lang.String channelId);

    List<BillPo> findAll4Pd(@Param("channelId") java.lang.String channelId, @Param("billDate") LocalDate billDate);

}
