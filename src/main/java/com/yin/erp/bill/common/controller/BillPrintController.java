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
import java.util.HashMap;
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
    private Map<String, String> map = new HashMap<String, String>() {{
        put("purchase", "采购单");
        put("order", "订货单");
        put("delivery", "配货单");
        put("supplier2Warehouse", "厂家来货");
        put("warehouse2Channel", "仓库出货");
        put("warehouse2Supplier", "仓库退货");
        put("inWarehouse", "仓库收退货");
        put("warehouseInventory", "仓库盘点");
        put("warehouseLoss", "仓库损益");
        put("inChannel", "渠道收货");
        put("channel2ChannelOut", "渠道调出");
        put("channel2ChannelIn", "渠道调入");
        put("channel2Warehouse", "渠道退货");
        put("supplier2Channel", "渠道采购收货");
        put("channel2Supplier", "渠道退货退货");
        put("channelInventory", "渠道盘点");
        put("channelLoss", "渠道损益");
        put("noticeChannel2ChannelOut", "渠道调出通知单");
    }};


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
    public String print(Model model, String code, String type, String paperType) throws MessageException {
        Map<String, BaseBillDao> beans = context.getBeansOfType(BaseBillDao.class);
        System.out.println(beans.keySet());
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
        System.out.println(billPo);
        System.out.println(billGoodsPoList);
        System.out.println(billDetailPoList);
        billDetailPoList.stream().forEach(d -> d.setBillOrder(billGoodsPoList.stream().filter(g -> g.getGoodsId().equals(d.getGoodsId())).findFirst().get().getBillOrder()));
        billDetailPoList.sort((a, b) -> a.getBillOrder() > b.getBillOrder() ? 1 : -1);
        model.addAttribute("billName", map.get(type));
        model.addAttribute("billPo", billPo);
        model.addAttribute("billGoodsPoList", billGoodsPoList);
        model.addAttribute("billDetailPoList", billDetailPoList);
        model.addAttribute("totalGoodsAmount", billDetailPoList.stream().map(d -> d.getPrice().multiply(BigDecimal.valueOf(d.getBillCount()))).reduce((a, b) -> a.add(b)).get());
        model.addAttribute("totalGoodsCount", billDetailPoList.stream().map(d -> d.getBillCount()).reduce((a, b) -> a + b).get());
        if ("A4".equals(paperType)) {
            return "print/bill/print_a4";
        } else if ("80".equals(paperType)) {
            return "print/bill/print_80";
        } else {
            return "print/bill/print_58";
        }
    }

}
