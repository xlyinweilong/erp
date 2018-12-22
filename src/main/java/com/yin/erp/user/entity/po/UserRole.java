package com.yin.erp.user.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 用户与角色的关系
 */
@Entity
@Table(name = "u_user_role")
@Getter
@Setter
public class UserRole extends BasePo {
    @Column(name = "user_id")
    private long userId;

    @Column(name = "role_id")
    private long roleId;

}
