package com.yin.erp.info.employ.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 营业员
 *
 * @author yin
 */
@Entity
@Table(name = "i_employ", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class EmployPo extends BasePo {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

}
