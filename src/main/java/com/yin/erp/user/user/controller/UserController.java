package com.yin.erp.user.user.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.user.role.dao.RoleDao;
import com.yin.erp.user.user.dao.UserDao;
import com.yin.erp.user.user.entity.po.UserPo;
import com.yin.erp.user.user.entity.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/user/user")
@Transactional(rollbackFor = Throwable.class)
public class UserController {

    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;


    /**
     * 列表
     *
     * @return
     */
    @PostMapping(value = "list", consumes = "application/json")
    public BaseJson list(@RequestBody UserVo vo) throws MessageException {
        Page page = userDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getSearchKey())) {
                List<Predicate> predicatesSearch = new ArrayList<>();
                predicatesSearch.add(criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%"));
                predicatesSearch.add(criteriaBuilder.like(root.get("account"), "%" + vo.getSearchKey() + "%"));
                predicates.add(criteriaBuilder.or(predicatesSearch.toArray(new Predicate[predicatesSearch.size()])));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        BackPageVo<UserVo> back = new BackPageVo();
        back.setTotalElements(page.getTotalElements());
        List<UserVo> list = new ArrayList<>();
        for (Object o : page.getContent()) {
            UserVo userVo = new UserVo();
            CopyUtil.copyProperties(userVo, o);
            list.add(userVo);
            userVo.setRoleName(roleDao.findById(((UserPo) o).getRoleId()).get().getName());
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
    public BaseJson save(@Validated @RequestBody UserVo vo) throws MessageException {
        UserPo userPo = new UserPo();
        if (StringUtils.isNotBlank(vo.getId())) {
            userPo = userDao.findById(vo.getId()).get();
        }
        if (userDao.findByAccount(vo.getAccount()) != null && !vo.getAccount().equals(userPo.getAccount())) {
            throw new MessageException("账号已经存在");
        }
        userPo.setRoleId(vo.getRoleId());
        userPo.setName(vo.getName());
        userPo.setAccount(vo.getAccount());
        userPo.setPasswd(vo.getPasswd());
        userDao.save(userPo);
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
            userDao.deleteById(id);
        }
        return BaseJson.getSuccess("删除成功");
    }

}
