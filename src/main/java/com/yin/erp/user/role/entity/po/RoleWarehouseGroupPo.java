package com.yin.erp.user.role.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.common.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 角色菜单按钮权限表
 */
@Entity
@Table(name = "u_role_warehouse_group")
@Getter
@Setter
public class RoleWarehouseGroupPo extends BaseDataPo {

    @Column(name="role_id")
    private String roleId;

    @Column(name="warehouse_group_id")
    private String warehouseGroupId;

    @JsonIgnore
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", updatable = false, insertable = false)
    private RolePo rolePo;

    public RoleWarehouseGroupPo() {
    }

    public RoleWarehouseGroupPo(String roleId) {
        this.roleId = roleId;
    }

    public RoleWarehouseGroupPo(String roleId, String warehouseGroupId) {
        this.roleId = roleId;
        this.warehouseGroupId = warehouseGroupId;
    }
}
