package com.yin.erp.user.user.controller;

import com.yin.erp.base.anno.LoginAnno;
import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.user.role.dao.*;
import com.yin.erp.user.user.dao.UserDao;
import com.yin.erp.user.user.entity.po.UserPo;
import com.yin.erp.user.user.entity.vo.in.LoginUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/user/login")
public class LoginController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RolePowerDao rolePowerDao;
    @Autowired
    private RoleGoodsGroupDao roleGoodsGroupDao;
    @Autowired
    private RoleWarehouseGroupDao roleWarehouseGroupDao;
    @Autowired
    private RoleChannelGroupDao roleChannelGroupDao;
    @Autowired
    private RoleSupplierGroupDao roleSupplierGroupDao;

    /**
     * 登录
     *
     * @param loginUserVo
     * @return
     * @throws Exception
     */
    @LoginAnno
    @PostMapping(value = "login", consumes = "application/json")
    public BaseJson login(@Validated @RequestBody LoginUserVo loginUserVo, HttpSession session) throws Exception {
        UserPo user = userDao.findByAccount(loginUserVo.getUsername());
        if (user == null) {
            throw new MessageException("账号错误！");
        }
        if (!user.getPasswd().equals(loginUserVo.getPassword())) {
            throw new MessageException("密码错误！");
        }
        UserSessionBo bo = new UserSessionBo();
        String uuid = UUID.randomUUID().toString();
        bo.setToken(uuid);
        bo.setId(user.getId());
        bo.setName(user.getName());
        List<String> roles = new ArrayList<>();
        roles.add("admin");
        bo.setRoles(roles);
        //获取角色菜单权限
        bo.setPowers(rolePowerDao.findPowerIdByRoleId(user.getRoleId()));
        //获取角色的数据范围
        bo.setGoodsGroupIds(roleGoodsGroupDao.findGoodsGroupIdByRoleId(user.getRoleId()));
        bo.setChannelGroupIds(roleChannelGroupDao.findChannelGroupIdByRoleId(user.getRoleId()));
        bo.setWarehouseGroupIds(roleWarehouseGroupDao.findWarehouseGroupIdByRoleId(user.getRoleId()));
        bo.setSupplierGroupIds(roleSupplierGroupDao.findSupplierGroupIdByRoleId(user.getRoleId()));
        redisTemplate.opsForValue().set(uuid, bo, 30L, TimeUnit.MINUTES);
        session.setAttribute("user", bo);
        return BaseJson.getSuccess("登录成功", bo);
    }

    /**
     * 用户活着
     *
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "keep_alive")
    public BaseJson keepAlive(HttpServletRequest request) throws Exception {
        return BaseJson.getSuccess();
    }

    /**
     * 个人信息信息
     *
     * @param session
     * @return
     * @throws Exception
     */
    @GetMapping(value = "info")
    public BaseJson info(HttpSession session, HttpServletRequest request) throws Exception {
        String token = request.getHeader("X-Token");
        return BaseJson.getSuccess(redisTemplate.opsForValue().get(token));
    }

    /**
     * 登出
     *
     * @param session
     * @return
     * @throws Exception
     */
    @LoginAnno
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public BaseJson logout(HttpSession session) throws Exception {
        session.removeAttribute("user");
        return BaseJson.getSuccess();
    }

    /**
     * 重置密码
     *
     * @param session
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "reset_password", method = RequestMethod.POST, consumes = "application/json")
    public BaseJson resetPassword(HttpSession session, @RequestBody String password) throws Exception {
        UserSessionBo userBo = (UserSessionBo) session.getAttribute("user");
//        UserPo user = userDao.findById(userBo.getId()).get();
//        user.setPasswd(password);
//        userDao.save(user);
        return BaseJson.getSuccess();
    }

    //用户菜单

    //用户权力

}
