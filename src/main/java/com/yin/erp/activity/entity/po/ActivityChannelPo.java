package com.yin.erp.activity.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 促销活动的渠道
 *
 * @author yin
 */
@Entity
@Table(name = "activity_channel")
@Getter
@Setter
public class ActivityChannelPo extends BaseDataPo {

    /**
     * 活动id
     */
    @Column(name = "activity_Id")
    private String activityId;

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

    @JsonIgnore
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_Id", updatable = false, insertable = false)
    private ActivityPo activityPo;

}
