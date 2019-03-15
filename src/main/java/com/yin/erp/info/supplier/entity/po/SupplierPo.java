package com.yin.erp.info.supplier.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 供应商
 *
 * @author yin
 */
@Entity
@Table(name = "i_supplier", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class SupplierPo extends BasePo {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;


    @Column(name = "group_id")
    private String groupId;


    @Column(name = "group_name")
    private String groupName;

}
