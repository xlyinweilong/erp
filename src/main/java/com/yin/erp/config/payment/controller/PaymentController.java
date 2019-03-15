package com.yin.erp.config.payment.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.config.payment.dao.PaymentDao;
import com.yin.erp.config.payment.entity.po.PaymentPo;
import com.yin.erp.pos.cash.dao.PosCashPaymentDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 支付方式
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/config/payment")
@Transactional(rollbackFor = Throwable.class)
public class PaymentController {

    @Autowired
    private PaymentDao paymentDao;
    @Autowired
    private PosCashPaymentDao posCashPaymentDao;

    /**
     * 保存
     *
     * @param po
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@RequestBody PaymentPo po) throws MessageException {
        PaymentPo dbPo = new PaymentPo();
        if (StringUtils.isNotBlank(po.getId())) {
            dbPo = paymentDao.findById(po.getId()).get();
        }
        dbPo.setName(po.getName());
        dbPo.setStartUp(po.getStartUp());
        paymentDao.save(dbPo);
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
        List<PaymentPo> page = paymentDao.findAll(new Sort(Sort.Direction.ASC, "orderIndex"));
        return BaseJson.getSuccess(page);
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            PaymentPo paymentPo = paymentDao.findById(id).get();
            if (paymentPo.getSys() == 1) {
                throw new MessageException("系统默认无法删除");
            }
            if (posCashPaymentDao.countByPaymentId(id) > 0L) {
                throw new MessageException("数据被引用，无法删除");
            }
            paymentDao.deleteById(id);
        }
        return BaseJson.getSuccess();
    }

}
