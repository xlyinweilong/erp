package com.yin.erp.user.user.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.common.entity.po.BasePo;
import com.yin.erp.user.diy.entity.po.UserDiyPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * 用户表
 */
@Entity
@Table(name = "u_user")
@Getter
@Setter
public class UserPo extends BasePo {

    @Column(name="name")
    private String name;

    @Column(name="account")
    private String account;

    @Column(name="passwd")
    private String passwd;

    @Column(name="role_id")
    private String roleId;

    @JsonIgnore
    @OneToMany(mappedBy = "userPo", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<UserDiyPo> userDiyPo;

}
