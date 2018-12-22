package com.yin.erp.user.dao;


import com.yin.erp.user.entity.po.UserPo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * 用户仓库
 */
//@RepositoryRestResource(collectionResourceRel = "user", path = "user")
@Transactional
@Repository
public interface UserDao extends PagingAndSortingRepository<UserPo, String> {

//    List<Menu> findById(@Param("id") long id);

    UserPo findByAccount(String account);

//    @RequestMapping("/delete")
//    @Query("update Menu u set u.delete = true")@

//    int update(@Param("id") long id);

}
