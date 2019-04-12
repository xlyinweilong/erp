package com.yin.erp.pos.back.entity.vo.in;

import com.yin.common.entity.vo.BaseVo;
import com.yin.erp.pos.cash.entity.po.PosCashPaymentPo;
import com.yin.erp.vip.info.entity.po.VipPo;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 退货VO
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class BackPaymentVo extends BaseVo {

    /**
     * 总金额
     */
    private BigDecimal totalAmount;

    /**
     * 总需要退货的金额
     */
    private BigDecimal totalBackAmount;

    /**
     * 会员
     */
    private VipPo vip;

    /**
     * 支付方式
     */
    private List<PosCashPaymentPo> paymentList;

}
