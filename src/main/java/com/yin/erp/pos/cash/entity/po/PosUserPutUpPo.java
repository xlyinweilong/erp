package com.yin.erp.pos.cash.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 挂单
 */
@Entity
@Table(name = "bill_pos_user_put_up")
@Getter
@Setter
public class PosUserPutUpPo extends BasePo {

    /**
     * 单号
     */
    @Column(name = "code")
    private String code;

    /**
     * json存贮的内容
     */
    @Column(name = "json")
    private String json;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;

}
