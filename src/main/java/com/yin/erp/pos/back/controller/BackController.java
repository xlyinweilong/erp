package com.yin.erp.pos.back.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.exceptions.MessageException;
import com.yin.common.service.LoginService;
import com.yin.erp.pos.back.entity.vo.in.BackPayVo;
import com.yin.erp.pos.back.entity.vo.in.BackSearchVo;
import com.yin.erp.pos.back.service.BackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 退货控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/pos/back")
@Transactional(rollbackFor = Throwable.class)
public class BackController {

    @Autowired
    private BackService backService;
    @Autowired
    private LoginService loginService;

    /**
     * 查询销售
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "find_by_bill_code")
    public BaseJson findByBillCode(BackSearchVo vo, HttpServletRequest request) throws MessageException {
        return BaseJson.getSuccess(backService.findGoodsList(vo));
    }

    /**
     * 显示退还的支付方式
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "back_payment")
    public BaseJson backPayment(@Validated @RequestBody BackPayVo vo) throws MessageException {
        return BaseJson.getSuccess(backService.backPayment(vo));
    }

    /**
     * 退货
     *
     * @param vo
     * @param request
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "do_back")
    public BaseJson doBack(@Validated @RequestBody BackPayVo vo, HttpServletRequest request) throws MessageException {
        return BaseJson.getSuccess(backService.doBack(vo, loginService.getUserSession(request)));
    }

}
