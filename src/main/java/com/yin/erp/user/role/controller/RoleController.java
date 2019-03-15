package com.yin.erp.user.role.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.user.role.dao.RoleDao;
import com.yin.erp.user.role.entity.po.RolePo;
import com.yin.erp.user.role.entity.vo.RoleVo;
import com.yin.erp.user.user.dao.UserDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/user/role")
@Transactional(rollbackFor = Throwable.class)
public class RoleController {

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserDao userDao;


    /**
     * 列表
     *
     * @return
     */
    @PostMapping(value = "list", consumes = "application/json")
    public BaseJson list(@RequestBody RoleVo vo) throws MessageException {
        Page page = roleDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getName() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        BackPageVo<RoleVo> back = new BackPageVo();
        back.setTotalElements(page.getTotalElements());
        List<RoleVo> list = new ArrayList<>();
        for (Object o : page.getContent()) {
            RoleVo roleVo = new RoleVo();
            CopyUtil.copyProperties(roleVo, o);
            list.add(roleVo);
        }
        back.setContent(list);
        return BaseJson.getSuccess(back);
    }

    /**
     * 保存
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody RoleVo vo) throws MessageException {
        RolePo rolePo = new RolePo();
        if (StringUtils.isNotBlank(vo.getId())) {
            rolePo = roleDao.findById(vo.getId()).get();
        }
        rolePo.setName(vo.getName());
        roleDao.save(rolePo);
        return BaseJson.getSuccess("保存成功");
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            roleDao.deleteById(id);
            //查看引用情况
            if (userDao.countUserPoByRoleId(id) > 0) {
                throw new MessageException("数据被引用，无法删除");
            }
        }
        return BaseJson.getSuccess("删除成功");
    }

}
