package com.yin.erp.pos.cash.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 销售营业员
 */
@Entity
@Table(name = "bill_pos_cash_employ")
@Getter
@Setter
public class PosCashEmployPo extends BasePo {

    /**
     * 销售ID
     */
    @Column(name = "bill_id")
    private String billId;

    /**
     * 单据时间
     */
    @Column(name = "bill_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date billDate;

    /**
     * 销售渠道
     */
    @Column(name = "channel_id")
    private String channelId;

    /**
     * 营业员ID
     */
    @Column(name = "employ_id")
    private String employId;

    /**
     * 营业员编号
     */
    @Column(name = "employ_code")
    private String employCode;

    /**
     * 营业员名称
     */
    @Column(name = "employ_name")
    private String employName;

    /**
     * 分成比例
     */
    @Column(name = "rate")
    private BigDecimal rate;

}
