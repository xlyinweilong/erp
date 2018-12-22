package com.yin.erp.user.service;

import com.yin.erp.base.feign.user.bo.UserSessionBo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserService {

    @Autowired
    private RedisTemplate redisTemplate;

    public UserSessionBo getUserSession(HttpServletRequest request) {
        String key = "X-Token";
        String token = request.getHeader(key);
        if (StringUtils.isBlank(token)) {
            for (Cookie cookie : request.getCookies()) {
                if (key.equals(cookie.getName())) {
                    token = cookie.getValue();
                }
                if ("user".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }
        if (StringUtils.isBlank(token)) {
            token = request.getParameter("token");
        }
        if (StringUtils.isBlank(token)) {
            return null;
        }
        ValueOperations<String, UserSessionBo> operations = redisTemplate.opsForValue();
        UserSessionBo bo = operations.get(token);
        if (bo != null) {
            operations.set(token, bo, 30, TimeUnit.MINUTES);
        }
        return bo;
    }

    //login

    //logout

    //reset password

    //my info

//    public
}
