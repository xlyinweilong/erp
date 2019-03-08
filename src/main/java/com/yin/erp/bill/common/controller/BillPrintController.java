package com.yin.erp.bill.common.controller;


import com.yin.erp.base.anno.LoginAnno;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.bill.common.dao.BaseBillDao;
import com.yin.erp.bill.common.dao.BaseBillDetailDao;
import com.yin.erp.bill.common.dao.BaseBillGoodsDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 单据打印
 *
 * @author yin
 */
@Controller
@RequestMapping(value = "api/bill/print")
public class BillPrintController {

    @Autowired
    private ApplicationContext context;


    /**
     * 单据打印
     *
     * @param model
     * @param code
     * @param type
     * @return
     * @throws MessageException
     */
    @LoginAnno
    @GetMapping(value = "print")
    public String print(Model model, String code, String type) throws MessageException {
        Map<String, BaseBillDao> beans = context.getBeansOfType(BaseBillDao.class);
        Map<String, BaseBillGoodsDao> goodsBeans = context.getBeansOfType(BaseBillGoodsDao.class);
        Map<String, BaseBillDetailDao> detailBeans = context.getBeansOfType(BaseBillDetailDao.class);
        BaseBillDao baseBillDao = beans.get(type + "Dao");
        BaseBillGoodsDao baseBillGoodsDao = goodsBeans.get(type + "GoodsDao");
        BaseBillDetailDao baseBillDetailDao = detailBeans.get(type + "DetailDao");
        //查询单据
        BillPo billPo = baseBillDao.findByCode(code);
        //查询单据货品
        List<BillGoodsPo> billGoodsPoList = baseBillGoodsDao.findByBillId(billPo.getId());
        List<BillDetailPo> billDetailPoList = baseBillDetailDao.findByBillId(billPo.getId());
//        model.addAttribute("billName", billPo);
        model.addAttribute("billPo", billPo);
        model.addAttribute("billGoodsPoList", billGoodsPoList);
        model.addAttribute("billDetailPoList", billDetailPoList);
        model.addAttribute("totalGoodsAmount", billDetailPoList.stream().map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getBillCount()))).reduce((a, b) -> a.add(b)).get());
        model.addAttribute("totalGoodsCount", billDetailPoList.stream().map(d -> d.getBillCount()).reduce((a, b) -> a + b).get());
        return "print/bill/print";
    }

}
