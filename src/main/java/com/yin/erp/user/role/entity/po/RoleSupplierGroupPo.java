package com.yin.erp.user.role.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 角色供应商范围
 */
@Entity
@Table(name = "u_role_supplier_group")
@Getter
@Setter
public class RoleSupplierGroupPo extends BaseDataPo {

    @Column(name="role_id")
    private String roleId;

    @Column(name="supplier_group_id")
    private String supplierGroupId;

    public RoleSupplierGroupPo() {
    }

    public RoleSupplierGroupPo(String roleId) {
        this.roleId = roleId;
    }

    public RoleSupplierGroupPo(String roleId, String supplierGroupId) {
        this.roleId = roleId;
        this.supplierGroupId = supplierGroupId;
    }
}
