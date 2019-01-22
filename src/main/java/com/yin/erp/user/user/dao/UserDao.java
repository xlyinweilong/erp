package com.yin.erp.user.user.dao;


import com.yin.erp.user.user.entity.po.UserPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 用户仓库
 */
@org.springframework.transaction.annotation.Transactional(rollbackFor = Throwable.class)
@Repository
public interface UserDao extends JpaRepository<UserPo, String>, JpaSpecificationExecutor {

    /**
     * 根据账户查询用户
     *
     * @param account
     * @return
     */
    UserPo findByAccount(String account);


    /**
     * 查询数量
     *
     * @param roleId
     * @return
     */
    int countUserPoByRoleId(String roleId);


}
