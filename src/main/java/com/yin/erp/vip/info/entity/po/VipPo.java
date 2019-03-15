package com.yin.erp.vip.info.entity.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 会员信息
 *
 * @author yin
 */
@Entity
@Table(name = "vip")
@Getter
@Setter
public class VipPo extends BasePo {


    /**
     * 会员编号
     */
    @Column(name = "code")
    private String code;

    /**
     * 会员名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 会员性别
     */
    @Column(name = "sex")
    private Integer sex = -1;

    /**
     * 余额
     */
    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 积分
     */
    @Column(name = "integral")
    private Integer integral = 0;

    /**
     * 经验值
     */
    @Column(name = "xp_value")
    private Integer xpValue = 0;

    /**
     * 开发渠道
     */
    @Column(name = "open_channel_id")
    private String openChannelId;

    @Column(name = "open_channel_code")
    private String openChannelCode;

    @Column(name = "open_channel_name")
    private String openChannelName;

    /**
     * 开发员工
     */
    @Column(name = "open_employ_id")
    private String openEmployId;

    @Column(name = "open_employ_code")
    private String openEmployCode;

    @Column(name = "open_employ_name")
    private String openEmployName;

    /**
     * 开发日期
     */
    @Column(name = "open_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate openDate;


}
