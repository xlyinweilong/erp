package com.yin.erp.user.diy.dao;


import com.yin.erp.user.diy.entity.po.UserDiyPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户偏好
 *
 * @author yin
 */
@Repository
public interface UserDiyDao extends JpaRepository<UserDiyPo, String>, JpaSpecificationExecutor {

    /**
     * 删除用户的偏好
     *
     * @param userId
     * @return
     */
    @Modifying
    @Query("delete from UserDiyPo t where t.userId = :userId AND type = :type")
    int deleteAllByUserIdAndType(@Param("userId") String userId,@Param("type") String type);

    /**
     * 查询用户的偏好
     *
     * @param userId
     * @return
     */
    @Query("select t.key from UserDiyPo t where t.userId = :userId AND t.type = :type")
    List<String> findSaleKeyByUserIdAndType(@Param("userId") String userId,@Param("type") String type);
}
