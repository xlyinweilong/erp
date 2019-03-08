package com.yin.erp.user.role.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * 角色表
 */
@Entity
@Table(name = "u_role")
@Getter
@Setter
public class RolePo extends BasePo {

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "rolePo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<RolePowerPo> rolePowerPoList;

    @JsonIgnore
    @OneToMany(mappedBy = "rolePo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<RoleSupplierGroupPo> roleSupplierGroupPoList;

    @JsonIgnore
    @OneToMany(mappedBy = "rolePo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<RoleWarehouseGroupPo> roleWarehouseGroupPoList;

    @JsonIgnore
    @OneToMany(mappedBy = "rolePo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<RoleGoodsGroupPo> roleGoodsGroupPoList;

    @JsonIgnore
    @OneToMany(mappedBy = "rolePo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<RoleChannelGroupPo> roleChannelGroupPoList;

}
