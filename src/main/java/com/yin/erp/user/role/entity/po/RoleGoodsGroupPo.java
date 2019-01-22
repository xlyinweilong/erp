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
@Table(name = "u_role_goods_group")
@Getter
@Setter
public class RoleGoodsGroupPo extends BaseDataPo {

    @Column(name="role_id")
    private String roleId;

    @Column(name="goods_group_id")
    private String goodsGroupId;

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
