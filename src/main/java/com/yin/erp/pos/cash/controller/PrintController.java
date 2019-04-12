package com.yin.erp.pos.cash.controller;

import com.yin.common.anno.LoginAnno;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.pos.cash.dao.PosCashDao;
import com.yin.erp.pos.cash.dao.PosCashDetailDao;
import com.yin.erp.pos.cash.dao.PosCashPaymentDao;
import com.yin.erp.pos.cash.entity.po.PosCashDetailPo;
import com.yin.erp.pos.cash.entity.po.PosCashPaymentPo;
import com.yin.erp.pos.cash.entity.po.PosCashPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 销售控制器
 *
 * @author yin
 */
@Controller
@RequestMapping(value = "api/pos/cash")
@Transactional(rollbackFor = Throwable.class)
public class PrintController {

    @Autowired
    private PosCashDao posCashDao;
    @Autowired
    private PosCashDetailDao posCashDetailDao;
    @Autowired
    private PosCashPaymentDao posCashPaymentDao;


    /**
     * POS支付小票打印页面
     *
     * @param code
     * @return
     * @throws MessageException
     */
    @LoginAnno
    @GetMapping(value = "print")
    public String print(Model model, String code) throws MessageException {
        //查询单据
        PosCashPo posCashPo = posCashDao.findByCode(code);
        //查询单据货品
        List<PosCashDetailPo> posCashDetailPoList = posCashDetailDao.findBackByBillId(posCashPo.getId());
        //查询支付方式
        List<PosCashPaymentPo> posCashPaymentPoList = posCashPaymentDao.findAllByBillId(posCashPo.getId());
        //查询会员信息、余额、积分情况
        //查询会员本次获得积分
        model.addAttribute("totalGoodsAmount", posCashDetailPoList.isEmpty() ? 0 : posCashDetailPoList.stream().map(d -> d.getAmount()).reduce((a, b) -> a.add(b)).get());
        model.addAttribute("totalGoodsCount", posCashDetailPoList.isEmpty() ? 0 : posCashDetailPoList.stream().map(d -> d.getBillCount()).reduce((a, b) -> a + b).get());
        model.addAttribute("totalGetIntegral", posCashPo.getVipId() == null ? 0 : posCashDetailPoList.stream().map(d -> d.getIntegral()).reduce((a, b) -> a + b).get());
        model.addAttribute("pos", posCashPo);
        model.addAttribute("goodsList", posCashDetailPoList);
        model.addAttribute("paymentList", posCashPaymentPoList);
        return "print/pos/print";
    }


}
