package com.yin.erp.user.role.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 权利
 */
@Entity
@Table(name = "u_power")
@Getter
@Setter
public class PowerPo extends BaseDataPo {

    @Column(name="name")
    private String name;

    @Column(name = "menu_id")
    private String menuId;

}
