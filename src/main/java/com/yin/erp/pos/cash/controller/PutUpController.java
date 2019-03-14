package com.yin.erp.pos.cash.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.pos.cash.dao.PosUserPutUpDao;
import com.yin.erp.pos.cash.entity.po.PosUserPutUpPo;
import com.yin.erp.pos.cash.entity.vo.in.PutUpInVo;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 挂单
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/pos/put_up")
@Transactional(rollbackFor = Throwable.class)
public class PutUpController {

    @Autowired
    private PosUserPutUpDao posUserPutUpDao;
    @Autowired
    private LoginService loginService;

    /**
     * 挂入
     *
     * @param putUpInVo
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "in", consumes = "application/json")
    public BaseJson in(@Validated @RequestBody PutUpInVo putUpInVo, HttpServletRequest request) throws Exception {
        UserSessionBo user = loginService.getUserSession(request);
        PosUserPutUpPo posUserPutUpPo = new PosUserPutUpPo();
        posUserPutUpPo.setUserId(user.getId());
        posUserPutUpPo.setCode(putUpInVo.getCode());
        posUserPutUpPo.setJson(putUpInVo.getJson());
        posUserPutUpDao.save(posUserPutUpPo);
        return BaseJson.getSuccess();
    }

    /**
     * 查询挂单列表
     *
     * @param request
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "list")
    public BaseJson list(HttpServletRequest request) throws MessageException {
        UserSessionBo user = loginService.getUserSession(request);
        return BaseJson.getSuccess(posUserPutUpDao.findByUserId(user.getId(), PageRequest.of(0, 10, Sort.Direction.DESC, "createDate")));
    }

    /**
     * 挂出
     *
     * @param putUpInVo
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "out", consumes = "application/json")
    public BaseJson out(@RequestBody PutUpInVo putUpInVo, HttpServletRequest request) throws Exception {
        posUserPutUpDao.deleteById(putUpInVo.getId());
        return BaseJson.getSuccess();
    }

}
