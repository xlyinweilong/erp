package com.yin.erp.info.dict.entity.po;

import com.yin.common.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 字典
 *
 * @author yin
 */
@Entity
@Table(name = "dict", uniqueConstraints = {@UniqueConstraint(columnNames = {"code", "name", "type1", "type2"})})
@Getter
@Setter
public class DictPo extends BasePo {

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "type1")
    private String type1;

    @Column(name = "type2")
    private String type2;

    @Column(name = "order_index")
    private Integer orderIndex = 0;

    public DictPo() {
    }

    public DictPo(String code, String name, String type1, String type2) {
        this.code = code;
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
    }

    public DictPo(String name, String type1, String type2) {
        this.name = name;
        this.type1 = type1;
        this.type2 = type2;
    }
}
