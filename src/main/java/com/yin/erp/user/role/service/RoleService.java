package com.yin.erp.user.role.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class RoleService {

    @Autowired
    private RedisTemplate redisTemplate;

    //login

    //logout

    //reset password

    //my info

//    public
}
