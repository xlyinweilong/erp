package com.yin.erp.config.payment.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 会员信息
 *
 * @author yin
 */
@Entity
@Table(name = "config_payment")
@Getter
@Setter
public class PaymentPo extends BaseDataPo {

    @Column(name = "name")
    private String name;

    @Column(name = "sys")
    private Integer sys = 0;

    @Column(name = "start_up")
    private Integer startUp;

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    @Column(name = "sys_type")
    private String sysType = "DIY";

}
