package com.yin.erp.pos.cash.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * 销售
 */
@Entity
@Table(name = "bill_cash")
@Getter
@Setter
public class CashPo extends BasePo {

    /**
     * 单号
     */
    @Column(name = "code")
    private String code;

    /**
     * 单据时间
     */
    @Column(name = "bill_date")
    private LocalDate billDate;


    /**
     * 渠道ID
     */
    @Column(name = "channel_id")
    private String channelId;

    /**
     * 渠道编号
     */
    @Column(name = "channel_code")
    private String channelCode;

    /**
     * 渠道名称
     */
    @Column(name = "channel_name")
    private String channelName;

    /**
     * 总金额
     */
    @Column(name = "total_amount")
    private BigDecimal totalAmount = BigDecimal.ZERO;

    /**
     * 总吊牌额
     */
    @Column(name = "total_tag_amount")
    private BigDecimal totalTagAmount = BigDecimal.ZERO;

    /**
     * 总货品数量
     */
    @Column(name = "total_count")
    private Integer totalCount = 0;

    /**
     * 创建人ID
     */
    @Column(name = "create_user_id")
    private String createUserId;

    /**
     * 创建人名称
     */
    @Column(name = "create_user_name")
    private String createUserName;

    /**
     * 审核人ID
     */
    @Column(name = "audit_user_id")
    private String auditUserId;

    /**
     * 审核人名称
     */
    @Column(name = "audit_user_name")
    private String auditUserName;

    /**
     * 审核人名称
     */
    @Column(name = "audit_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date auditDate;


}
