package com.yin.erp.user.role.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 角色菜单按钮权限表
 */
@Entity
@Table(name = "u_role_power")
@Getter
@Setter
public class RolePowerPo extends BaseDataPo {

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "power_id")
    private String powerId;

    @JsonIgnore
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", updatable = false, insertable = false)
    private RolePo rolePo;

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
