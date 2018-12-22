package com.yin.erp.user.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 角色表
 */
@Entity
@Table(name = "u_role")
@Getter
@Setter
public class Role extends BasePo {
    @Column(name="name")
    private String name;

    @Column(name="deleted")
    private boolean deleted;

}
