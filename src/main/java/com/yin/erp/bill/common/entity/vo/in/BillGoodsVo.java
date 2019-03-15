package com.yin.erp.bill.common.entity.vo.in;

import com.yin.common.entity.vo.BaseVo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 单据货品VO
 *
 * @author yin.weilong
 * @date 2018.12.14
 */
@Getter
@Setter
public class BillGoodsVo extends BaseVo {

    @NotNull(message = "请输入价格")
    @DecimalMin(value = "0.00", message = "单价需要大于0")
    private BigDecimal price;

    @NotNull(message = "请输入吊牌价")
    @DecimalMin(value = "0.00", message = "吊牌价需要大于0")
    private BigDecimal tagPrice;

    @NotNull(message = "请输入货品")
    @Size(min = 1, max = 32, message = "请输入货品")
    private String goodsId;

    private String goodsCode;

    private String goodsName;

    private Integer times;

    @NotNull(message = "请输入货品详情数量")
    private List<BillDetailVo> detail;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillGoodsVo that = (BillGoodsVo) o;
        return Objects.equals(goodsId, that.goodsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(goodsId);
    }
}
