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
@Table(name = "u_role_warehouse_group")
@Getter
@Setter
public class RoleWarehouseGroupPo extends BaseDataPo {

    @Column(name="role_id")
    private String roleId;

    @Column(name="warehouse_group_id")
    private String warehouseGroupId;

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
