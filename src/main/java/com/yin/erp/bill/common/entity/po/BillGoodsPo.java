package com.yin.erp.bill.common.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 单据货品实体
 *
 * @author yin.weilong
 * @date 2018.12.01
 */
@MappedSuperclass
@Getter
@Setter
public class BillGoodsPo extends BaseDataPo {

    /**
     * 单据时间
     */
    @Column(name = "bill_date")
    private LocalDate billDate;

    /**
     * 单据ID
     */
    @Column(name = "bill_id")
    private String billId;

    /**
     * 货品ID
     */
    @Column(name = "goods_id")
    private String goodsId;

    /**
     * 货品编号
     */
    @Column(name = "goods_code")
    private String goodsCode;

    /**
     * 货品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 价格
     */
    @Column(name = "price")
    private BigDecimal price;

    /**
     * 吊牌价
     */
    @Column(name = "tag_price")
    private BigDecimal tagPrice;

    /**
     * 数量
     */
    @Column(name = "bill_count")
    private Integer billCount = 0;
}
