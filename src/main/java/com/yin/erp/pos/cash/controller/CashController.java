package com.yin.erp.pos.cash.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.info.barcode.service.BarCodeService;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.goods.entity.vo.GoodsVo;
import com.yin.erp.info.goods.entity.vo.out.Goods4BillSearchVo;
import com.yin.erp.info.goods.service.GoodsService;
import com.yin.erp.pos.cash.service.CashService;
import com.yin.erp.stock.service.StockChannelService;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * 销售控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/pos/cash")
@Transactional(rollbackFor = Throwable.class)
public class CashController {

    @Autowired
    private CashService cashService;
    @Autowired
    private BarCodeService barCodeService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private StockChannelService stockChannelService;



    /**
     * 扫码
     *
     * @param code
     * @return
     */
    @GetMapping(value = "scan_code")
    public BaseJson scanCode(String code, HttpServletRequest request) throws MessageException {
        return BaseJson.getSuccess(barCodeService.findByCode(code, loginService.getUserSession(request)));
    }

    /**
     * 货品列表
     *
     * @param vo
     * @param request
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "goods_list")
    public BaseJson goodsList(GoodsVo vo, HttpServletRequest request) throws MessageException {
        Page<GoodsPo> page = goodsService.findGoodsPage(vo, loginService.getUserSession(request));
        BackPageVo backPageVo = new BackPageVo();
        backPageVo.setTotalElements(page.getTotalElements());
        List<Goods4BillSearchVo> content = new LinkedList<>();
        for (GoodsPo po : page.getContent()) {
            Goods4BillSearchVo goodsVo = new Goods4BillSearchVo();
            goodsVo.setTagPrice(po.getTagPrice1());
            goodsVo.setPrice(po.getTagPrice1());
            CopyUtil.copyProperties(goodsVo, po);
            content.add(goodsVo);
        }
        backPageVo.setContent(content);
        return BaseJson.getSuccess(backPageVo);
    }

}
