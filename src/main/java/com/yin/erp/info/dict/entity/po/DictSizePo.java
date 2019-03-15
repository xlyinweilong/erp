package com.yin.erp.info.dict.entity.po;

import com.yin.common.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * 字典尺码
 *
 * @author yin
 */
@Entity
@Table(name = "dict_size", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "group_id"})})
@Getter
@Setter
public class DictSizePo extends BaseDataPo {

    @Column(name = "name")
    private String name;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_id")
    private String groupId;

    @Column(name = "order_index")
    private Integer orderIndex;


}
