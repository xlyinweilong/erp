package com.yin.erp.bill.common.dao;

import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * 单据货品DAO
 *
 * @author yin.weilong
 * @date 2018.12.25
 */
@NoRepositoryBean
public interface BaseBillGoodsDao<T, String> extends JpaRepository<T, String>, JpaSpecificationExecutor {

    /**
     * 通过单据ID删除
     *
     * @param billId
     * @return
     */
    int deleteAllByBillId(String billId);


    List<BillGoodsPo> findByBillId(String billId);

    Long countByGoodsId(String goodsId);
}
