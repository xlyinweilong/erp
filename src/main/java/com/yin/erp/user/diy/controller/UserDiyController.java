package com.yin.erp.user.diy.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.user.diy.dao.UserDiyDao;
import com.yin.erp.user.diy.entity.po.UserDiyPo;
import com.yin.erp.user.diy.entity.vo.in.DiyVo;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户偏好
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/user/diy")
@Transactional(rollbackFor = Throwable.class)
public class UserDiyController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserDiyDao userDiyDao;

    /**
     * 获取偏好
     *
     * @param type
     * @param request
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(String type, HttpServletRequest request) {
        UserSessionBo user = loginService.getUserSession(request);
        return BaseJson.getSuccess(userDiyDao.findSaleKeyByUserIdAndType(user.getId(), type));
    }

    /**
     * 保存偏好
     *
     * @param diyVo
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@RequestBody @Validated DiyVo diyVo, HttpServletRequest request) throws Exception {
        UserSessionBo user = loginService.getUserSession(request);
        userDiyDao.deleteAllByUserIdAndType(user.getId(), diyVo.getType());
        for (String key : diyVo.getKeys()) {
            UserDiyPo po = new UserDiyPo();
            po.setKey(key);
            po.setUserId(user.getId());
            po.setType(diyVo.getType());
            userDiyDao.save(po);
        }
        return BaseJson.getSuccess();
    }

}
