package com.yin.erp.bill.settlement.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.erp.bill.common.entity.po.BillPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * 盘点结存
 *
 * @author yin
 */
@Entity
@Table(name = "bill_settlement")
@Getter
@Setter
public class SettlementPo extends BillPo {

    /**
     * 类型
     */
    @Column(name = "type")
    private String type;

    /**
     * 单号
     */
    @Column(name = "code")
    private String code;

    /**
     * 单据时间
     */
    @Column(name = "bill_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate billDate;

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
     * 仓库
     */
    @Column(name = "warehouse_id")
    private String warehouseId;

    @Column(name = "warehouse_name")
    private String warehouseName;

    @Column(name = "warehouse_code")
    private String warehouseCode;

    /**
     * 渠道
     */
    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_code")
    private String channelCode;

}
