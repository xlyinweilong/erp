package com.yin.erp.bill.common.entity.po;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * 单据实体
 *
 * @author yin.weilong
 * @date 2018.12.01
 */
@MappedSuperclass
@Getter
@Setter
public class BillQuotedPo extends BillPo {

    /**
     * 总引用数量
     */
    @Column(name = "total_quoted_count")
    private Integer totalQuotedCount = 0;

}
