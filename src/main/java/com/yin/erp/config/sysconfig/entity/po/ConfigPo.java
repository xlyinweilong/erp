package com.yin.erp.config.sysconfig.entity.po;

import com.yin.common.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 系统配置
 *
 * @author yin
 */
@Entity
@Table(name = "config")
@Getter
@Setter
public class ConfigPo extends BaseDataPo {

    @Column(name = "type")
    private String type;

    @Column(name = "name")
    private String name;

    @Column(name = "value_type")
    private Integer valueType;

    @Column(name = "value_select")
    private String valueSelect;

    @Column(name = "default_value")
    private Integer defaultValue;

    @Column(name = "order_index")
    private Integer orderIndex;

}
