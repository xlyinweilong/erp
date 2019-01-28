package com.yin.erp.pos.cash.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 销售偏好
 */
@Entity
@Table(name = "u_sale_diy")
@Getter
@Setter
public class UserSaleDiyPo extends BaseDataPo {

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 销售KEY
     */
    @Column(name = "sale_key")
    private String saleKey;


}
