package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.PosDayKnotsPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 日结
 *
 * @author yin
 */
@Repository
public interface PosDayKnotsDao extends JpaRepository<PosDayKnotsPo, String>, JpaSpecificationExecutor {

    /**
     * 根据单号查询
     *
     * @param channelId
     * @return
     */
    PosDayKnotsPo findFirstByChannelIdOrderByBillDateDesc( String channelId);


}
