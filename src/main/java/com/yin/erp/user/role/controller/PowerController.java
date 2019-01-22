package com.yin.erp.user.role.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.user.role.dao.RoleDao;
import com.yin.erp.user.role.entity.po.RolePo;
import com.yin.erp.user.user.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色权利控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/user/role_power")
@Transactional(rollbackFor = Throwable.class)
public class PowerController {

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserDao userDao;


    /**
     * 获取
     *
     * @param id
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(String id) throws MessageException {
        RolePo rolePo = roleDao.findById(id).get();
        return BaseJson.getSuccess(rolePo);
    }

}
