package com.yin.erp.user.role.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 角色菜单按钮权限表
 */
@Entity
@Table(name = "u_role_power")
@Getter
@Setter
public class RolePowerPo extends BaseDataPo {

    @Column(name="role_id")
    private String roleId;

    @Column(name="power_id")
    private String powerId;

    public RolePowerPo() {
    }

    public RolePowerPo(String roleId) {
        this.roleId = roleId;
    }

    public RolePowerPo(String roleId, String powerId) {
        this.roleId = roleId;
        this.powerId = powerId;
    }
}
