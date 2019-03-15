package com.yin.erp.vip.integral.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.exceptions.MessageException;
import com.yin.common.utils.GenerateUtil;
import com.yin.erp.vip.integral.dao.VipIntegralToAmountDao;
import com.yin.erp.vip.integral.entity.po.VipIntegralToAmountPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员积分兑换金额
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/vip/integral_to_amount")
@Transactional(rollbackFor = Throwable.class)
public class VipIntegralToAmountController {

    @Autowired
    private VipIntegralToAmountDao vipIntegralToAmountDao;

    /**
     * 保存
     *
     * @param list
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody List<VipIntegralToAmountPo> list) throws MessageException {
        vipIntegralToAmountDao.deleteAll();
        for (VipIntegralToAmountPo po : list) {
            po.setId(GenerateUtil.createUUID());
            vipIntegralToAmountDao.save(po);
        }
        return BaseJson.getSuccess();
    }

    /**
     * 获取
     *
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "list")
    public BaseJson list() throws MessageException {
        return BaseJson.getSuccess(vipIntegralToAmountDao.findAll());
    }

}
