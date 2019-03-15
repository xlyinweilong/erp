package com.yin.erp.activity.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.common.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 促销活动的会员
 *
 * @author yin
 */
@Entity
@Table(name = "activity_vip")
@Getter
@Setter
public class ActivityVipPo extends BaseDataPo {

    /**
     * 活动id
     */
    @Column(name = "activity_Id")
    private String activityId;

    /**
     * 会员等级ID
     */
    @Column(name = "grade_id")
    private String gradeId;


    @JsonIgnore
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_Id", updatable = false, insertable = false)
    private ActivityPo activityPo;

}
