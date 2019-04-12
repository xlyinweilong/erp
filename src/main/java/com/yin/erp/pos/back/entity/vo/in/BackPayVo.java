package com.yin.erp.pos.back.entity.vo.in;

import com.yin.common.entity.vo.BaseVo;
import com.yin.erp.pos.cash.entity.po.PosCashPaymentPo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 退货VO
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class BackPayVo extends BaseVo {

    /**
     * 单据编号
     */
    private String billCode;

    /**
     * 退回列表
     */
    private List<BackGoodsVo> goodsList;

    /**
     * 支付方式
     */
    private List<PosCashPaymentPo> paymentList;
}
