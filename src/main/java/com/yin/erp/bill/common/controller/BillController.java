package com.yin.erp.bill.common.controller;


import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.out.BaseUploadMessage;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 单据通用
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/bill/common")
public class BillController {

    @Autowired
    private LoginService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取导入状态
     *
     * @param request
     * @return
     */
    @GetMapping(value = "upload_status")
    public BaseJson uploadStatus(HttpServletRequest request, String key) {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        BaseUploadMessage baseUploadMessage = (BaseUploadMessage) redisTemplate.opsForValue().get(userSessionBo.getId() + ":upload:bill:" + key);
        return BaseJson.getSuccess(baseUploadMessage == null ? null : baseUploadMessage.getMessage(), baseUploadMessage);
    }

    /**
     * 重置导入状态
     *
     * @param request
     * @return
     */
    @GetMapping(value = "reset_upload_status")
    public BaseJson resetUploadStatus(HttpServletRequest request, String key) {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        redisTemplate.opsForValue().set(userSessionBo.getId() + ":upload:bill:" + key, new BaseUploadMessage(), 10L, TimeUnit.MINUTES);
        return BaseJson.getSuccess("操作成功");
    }

}
