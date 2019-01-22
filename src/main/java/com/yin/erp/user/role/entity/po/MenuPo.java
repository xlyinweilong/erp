package com.yin.erp.user.role.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 菜单
 */
@Entity
@Table(name = "u_menu")
@Getter
@Setter
public class MenuPo extends BaseDataPo {

    @Column(name = "name")
    private String name;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "parent_id")
    private String parentId;

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "is_dictionary")
    private boolean isDictionary;


}
