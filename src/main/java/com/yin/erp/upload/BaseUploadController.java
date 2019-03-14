package com.yin.erp.upload;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 上传通用控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/common/upload")
public class BaseUploadController {

    @Autowired
    private LoginService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查看上传状态
     *
     * @param key
     * @param request
     * @return
     */
    @GetMapping(value = "upload_status")
    public BaseJson uploadStatus(String key, HttpServletRequest request) throws Exception {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        return BaseJson.getSuccess(redisTemplate.opsForValue().get(userSessionBo.getId() + ":upload:" + key));
    }
}
