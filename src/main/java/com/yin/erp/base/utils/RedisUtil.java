package com.yin.erp.base.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * redis工具类
 * @author yin.weilong
 * @date 2018.12.20
 */
@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;
}
