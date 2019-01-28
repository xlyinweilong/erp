package com.yin.erp.pos.cash.dao;


import com.yin.erp.pos.cash.entity.po.UserSaleDiyPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 销售偏好
 *
 * @author yin
 */
@Repository
public interface UserSaleDiyDao extends JpaRepository<UserSaleDiyPo, String>, JpaSpecificationExecutor {

    /**
     * 删除用户的偏好
     *
     * @param userId
     * @return
     */
    @Modifying
    @Query("delete from UserSaleDiyPo t where t.userId = :userId")
    int deleteAllByUserId(@Param("userId") String userId);

    /**
     * 查询用户的偏好
     *
     * @param userId
     * @return
     */
    @Query("select t.saleKey from UserSaleDiyPo t where t.userId = :userId")
    List<String> findSaleKeyByUserId(@Param("userId") String userId);
}
