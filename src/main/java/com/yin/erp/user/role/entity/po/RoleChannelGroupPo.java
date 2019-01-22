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
@Table(name = "u_role_channel_group")
@Getter
@Setter
public class RoleChannelGroupPo extends BaseDataPo {

    @Column(name="role_id")
    private String roleId;

    @Column(name="channel_group_id")
    private String channelGroupId;

    public RoleChannelGroupPo() {
    }

    public RoleChannelGroupPo(String roleId) {
        this.roleId = roleId;
    }

    public RoleChannelGroupPo(String roleId, String channelGroupId) {
        this.roleId = roleId;
        this.channelGroupId = channelGroupId;
    }
}
