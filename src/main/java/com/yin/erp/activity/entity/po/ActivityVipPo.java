package com.yin.erp.activity.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

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


}
