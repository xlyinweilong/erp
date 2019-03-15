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
@Table(name = "u_role_goods_group")
@Getter
@Setter
public class RoleGoodsGroupPo extends BaseDataPo {

    @Column(name="role_id")
    private String roleId;

    @Column(name="goods_group_id")
    private String goodsGroupId;

    @JsonIgnore
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", updatable = false, insertable = false)
    private RolePo rolePo;

    public RoleGoodsGroupPo() {
    }

    public RoleGoodsGroupPo(String roleId) {
        this.roleId = roleId;
    }

    public RoleGoodsGroupPo(String roleId, String goodsGroupId) {
        this.roleId = roleId;
        this.goodsGroupId = goodsGroupId;
    }
}
