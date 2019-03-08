package com.yin.erp.bill.common.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * 单据详情实体
 *
 * @author yin.weilong
 * @date 2018.12.01
 */
@MappedSuperclass
@Getter
@Setter
public class BillDetailQuotedPo extends BillDetailPo {

    @Version
    private Long version;

    /**
     * 已经引用数量
     */
    @Column(name = "quoted_count")
    private Integer quotedCount = 0;
}
