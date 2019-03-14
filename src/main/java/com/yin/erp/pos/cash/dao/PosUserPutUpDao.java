package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.PosUserPutUpPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 挂单
 *
 * @author yin
 */
@org.springframework.transaction.annotation.Transactional(rollbackFor = Throwable.class)
@Repository
public interface PosUserPutUpDao extends JpaRepository<PosUserPutUpPo, String>, JpaSpecificationExecutor {

    /**
     * 查询挂单
     *
     * @param userId
     * @return
     */
    @Query("select t from PosUserPutUpPo t where t.userId = :userId order by t.createDate desc")
    Page<PosUserPutUpPo> findByUserId(@Param("userId") String userId, Pageable pageable);

}
