package com.yin.erp.pos.cash.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.exceptions.MessageException;
import com.yin.common.service.LoginService;
import com.yin.erp.pos.cash.dao.PosDayKnotsDao;
import com.yin.erp.pos.cash.entity.po.PosDayKnotsPo;
import com.yin.erp.pos.cash.entity.vo.in.PosDayKnotsInVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

/**
 * 日结
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/pos/day_knots")
@Transactional(rollbackFor = Throwable.class)
public class DayKnotsController {

    @Autowired
    private PosDayKnotsDao posDayKnotsDao;
    @Autowired
    private LoginService loginService;

    /**
     * 日结
     *
     * @param posDayKnotsInVo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "do_day_knots")
    public BaseJson doDayKnots(@RequestBody PosDayKnotsInVo posDayKnotsInVo, HttpServletRequest request) throws MessageException {
        UserSessionBo user = loginService.getUserSession(request);
        PosDayKnotsPo posDayKnotsPo = posDayKnotsDao.findFirstByChannelIdOrderByBillDateDesc(posDayKnotsInVo.getChannelId());
        PosDayKnotsPo po = new PosDayKnotsPo();
        if (posDayKnotsPo == null) {
            po.setBillDate(LocalDate.now());
        } else {
            po.setBillDate(posDayKnotsPo.getBillDate().plusDays(1L));
        }
        po.setChannelId(posDayKnotsInVo.getChannelId());
        po.setUserId(user.getId());
        posDayKnotsDao.save(po);
        return BaseJson.getSuccess(po);
    }

    /**
     * 取消日结
     *
     * @param posDayKnotsInVo
     * @param request
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "cancel_day_knots")
    public BaseJson cancelDayKnots(@RequestBody PosDayKnotsInVo posDayKnotsInVo, HttpServletRequest request) throws MessageException {
        PosDayKnotsPo posDayKnotsPo = posDayKnotsDao.findFirstByChannelIdOrderByBillDateDesc(posDayKnotsInVo.getChannelId());
        if (posDayKnotsPo != null) {
            posDayKnotsDao.delete(posDayKnotsPo);
        }
        return BaseJson.getSuccess();
    }


    /**
     * 最后的日结时间
     *
     * @param channelId
     * @param request
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "get_last")
    public BaseJson getLast(String channelId, HttpServletRequest request) throws MessageException {
        return BaseJson.getSuccess(posDayKnotsDao.findFirstByChannelIdOrderByBillDateDesc(channelId));
    }

}
