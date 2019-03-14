package com.yin.erp.info.marketpoint.entity.po;

import com.yin.erp.base.entity.po.BasePo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 商场扣点
 *
 * @author yin
 */
@Entity
@Table(name = "i_market_point", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"})})
@Getter
@Setter
public class MarketPointPo extends BasePo {

    /**
     * 扣点编号
     */
    @Column(name = "code")
    private String code;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 扣点
     */
    @Column(name = "point")
    private BigDecimal point;

    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

    @Column(name = "create_user_id")
    private String createUserId;

    @Column(name = "update_user_id")
    private String updateUserId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MarketPointPo barCodePo = (MarketPointPo) o;
        return Objects.equals(code, barCodePo.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), code);
    }
}
