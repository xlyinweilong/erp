package com.yin.erp.pos.cash.entity.vo.in;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 付款VO
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class PayVo {

    /**
     * 商品
     */
    private List<PosCashGoods> goodsList;

    /**
     * 支付方式
     */
    private List<PosCashPayment> paymentList;

    /**
     * 会员ID
     */
    private String vipId;

    private String vipCode;

    private String vipName;

    private String vipGradeId;

    /**
     * 渠道ID
     */
    private String channelId;

    private String channelName;

    private String channelCode;
}
