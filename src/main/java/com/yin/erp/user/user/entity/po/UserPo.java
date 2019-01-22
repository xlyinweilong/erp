package com.yin.erp.user.user.entity.po;

import lombok.Getter;
import lombok.Setter;
import com.yin.erp.base.entity.po.BasePo;
import javax.persistence.*;

/**
 * 用户表
 */
@Entity
@Table(name = "u_user")
@Getter
@Setter
public class UserPo extends BasePo{

    @Column(name="name")
    private String name;

    @Column(name="account")
    private String account;

    @Column(name="passwd")
    private String passwd;

    @Column(name="role_id")
    private String roleId;


}
