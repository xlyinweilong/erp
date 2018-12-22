package com.yin.erp.user.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 用户菜单关系表
 */
@Entity
@Table(name = "u_user_menu")
@Getter
@Setter
public class UserMenu extends BasePo {

    @Column(name = "user_id")
    private long userId;

    @Column(name = "menu_id")
    private long menuId;

}
