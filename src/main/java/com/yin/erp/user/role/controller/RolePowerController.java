package com.yin.erp.user.role.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.info.dict.entity.vo.DictVo;
import com.yin.erp.info.dict.service.DictService;
import com.yin.erp.user.role.dao.*;
import com.yin.erp.user.role.entity.po.*;
import com.yin.erp.user.role.entity.vo.RolePowerVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色权利控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/user/role_power")
@Transactional(rollbackFor = Throwable.class)
public class RolePowerController {

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private RolePowerDao rolePowerDao;
    @Autowired
    private RoleGoodsGroupDao roleGoodsGroupDao;
    @Autowired
    private RoleChannelGroupDao roleChannelGroupDao;
    @Autowired
    private RoleWarehouseGroupDao roleWarehouseGroupDao;
    @Autowired
    private RoleSupplierGroupDao roleSupplierGroupDao;
    @Autowired
    private MenuDao menuDao;
    @Autowired
    private PowerDao powerDao;
    @Autowired
    private DictService dictService;

    /**
     * 获取菜单和权限
     *
     * @return
     */
    @GetMapping(value = "power_and_menu")
    public BaseJson powerAndMenu() throws MessageException {
        List<MenuPo> menuList = menuDao.findAll(new Sort(Sort.Direction.ASC, "orderIndex"));
        List<PowerPo> powerList = powerDao.findAll();
        Map map = new HashMap();
        map.put("menuList", menuList);
        map.put("powerList", powerList);
        return BaseJson.getSuccess(map);
    }


    /**
     * 获取各种组列表
     *
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "group_list")
    public BaseJson groupList(String dictType1, String dictType2) throws MessageException {
        return BaseJson.getSuccess(dictService.findDictPage(new DictVo(dictType1, dictType2, 1, Integer.MAX_VALUE)));
    }

    /**
     * 保存
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody RolePowerVo vo) throws MessageException {

        rolePowerDao.deleteAllByRoleId(vo.getRoleId());
        List<RolePowerPo> rolePowerPoList = new ArrayList<>();
        vo.getSelectPowerKeys().forEach(key -> {
            RolePowerPo rolePowerPo = new RolePowerPo(vo.getRoleId(), key);
            rolePowerPoList.add(rolePowerPo);
        });
        rolePowerDao.saveAll(rolePowerPoList);

        roleGoodsGroupDao.deleteAllByRoleId(vo.getRoleId());
        List<RoleGoodsGroupPo> roleGoodsGroupPoList = new ArrayList<>();
        vo.getSelectGoodsGroupIds().forEach(id -> {
            RoleGoodsGroupPo roleGoodsGroupPo = new RoleGoodsGroupPo(vo.getRoleId(), id);
            roleGoodsGroupPoList.add(roleGoodsGroupPo);
        });
        roleGoodsGroupDao.saveAll(roleGoodsGroupPoList);

        roleChannelGroupDao.deleteAllByRoleId(vo.getRoleId());
        List<RoleChannelGroupPo> roleChannelGroupPoList = new ArrayList<>();
        vo.getSelectChannelGroupIds().forEach(id -> {
            RoleChannelGroupPo roleChannelGroupPo = new RoleChannelGroupPo(vo.getRoleId(), id);
            roleChannelGroupPoList.add(roleChannelGroupPo);
        });
        roleChannelGroupDao.saveAll(roleChannelGroupPoList);

        roleWarehouseGroupDao.deleteAllByRoleId(vo.getRoleId());
        List<RoleWarehouseGroupPo> roleWarehouseGroupPoList = new ArrayList<>();
        vo.getSelectWarehouseGroupIds().forEach(id -> {
            RoleWarehouseGroupPo roleWarehouseGroupPo = new RoleWarehouseGroupPo(vo.getRoleId(), id);
            roleWarehouseGroupPoList.add(roleWarehouseGroupPo);
        });
        roleWarehouseGroupDao.saveAll(roleWarehouseGroupPoList);

        roleSupplierGroupDao.deleteAllByRoleId(vo.getRoleId());
        List<RoleSupplierGroupPo> roleSupplierGroupPoList = new ArrayList<>();
        vo.getSelectSupplierGroupIds().forEach(id -> {
            RoleSupplierGroupPo roleSupplierGroupPo = new RoleSupplierGroupPo(vo.getRoleId(), id);
            roleSupplierGroupPoList.add(roleSupplierGroupPo);
        });
        roleSupplierGroupDao.saveAll(roleSupplierGroupPoList);

        return BaseJson.getSuccess("保存成功");
    }

    /**
     * 获取
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) throws MessageException {
        RolePo rolePo = roleDao.findById(id).get();
        RolePowerVo vo = new RolePowerVo();
        vo.setRoleId(rolePo.getId());
        vo.setRoleName(rolePo.getName());
        vo.setSelectPowerKeys(rolePowerDao.findPowerIdByRoleId(rolePo.getId()));
        vo.setSelectGoodsGroupIds(roleGoodsGroupDao.findGoodsGroupIdByRoleId(rolePo.getId()));
        vo.setSelectChannelGroupIds(roleChannelGroupDao.findChannelGroupIdByRoleId(rolePo.getId()));
        vo.setSelectWarehouseGroupIds(roleWarehouseGroupDao.findWarehouseGroupIdByRoleId(rolePo.getId()));
        vo.setSelectSupplierGroupIds(roleSupplierGroupDao.findSupplierGroupIdByRoleId(rolePo.getId()));
        return BaseJson.getSuccess(vo);
    }

}
