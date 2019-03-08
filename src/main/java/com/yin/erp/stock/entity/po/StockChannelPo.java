package com.yin.erp.stock.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 渠道库存
 *
 * @author yin
 */
@Entity
@Table(name = "stock_channel")
@Getter
@Setter
public class StockChannelPo extends BaseStockPo {

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "channel_code")
    private String channelCode;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "channel_group_id")
    private String channelGroupId;

}
