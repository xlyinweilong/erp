package com.yin.erp.pos.cash.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

/**
 * 日结
 */
@Entity
@Table(name = "bill_pos_day_knots")
@Getter
@Setter
public class PosDayKnotsPo extends BasePo {

    /**
     * 渠道ID
     */
    @Column(name = "channel_id")
    private String channelId;

    /**
     * 日结时间
     */
    @Column(name = "bill_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate billDate;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;

}
