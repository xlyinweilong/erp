package com.yin.erp.pos.cash.entity.vo.in;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * POS支付方式
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class PosCashPayment {

    private String id;

    private String name;

    private String sys;

    private String sysType;

    private BigDecimal amount;

    private Integer integral;
}
