package com.yin.erp.pos.cash.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.pos.cash.dao.UserSaleDiyDao;
import com.yin.erp.pos.cash.entity.po.UserSaleDiyPo;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 销售偏好
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/pos/sale_diy")
@Transactional(rollbackFor = Throwable.class)
public class CashSaleDiyController {

    @Autowired
    private LoginService loginService;
    @Autowired
    private UserSaleDiyDao userSaleDiyDao;

    @GetMapping(value = "list")
    public BaseJson list(HttpServletRequest request) {
        UserSessionBo user = loginService.getUserSession(request);
        return BaseJson.getSuccess(userSaleDiyDao.findSaleKeyByUserId(user.getId()));
    }

    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@RequestBody List<String> keys, HttpServletRequest request) throws Exception {
        UserSessionBo user = loginService.getUserSession(request);
        userSaleDiyDao.deleteAllByUserId(user.getId());
        for (String key : keys) {
            UserSaleDiyPo po = new UserSaleDiyPo();
            po.setSaleKey(key);
            po.setUserId(user.getId());
            userSaleDiyDao.save(po);
        }
        return BaseJson.getSuccess();
    }

}
