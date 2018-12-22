package com.yin.erp.info.goods.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * 货品内长度资料
 *
 * @author yin
 */
@Entity
@Table(name = "goods_in_size")
@Getter
@Setter
public class GoodsInSizePo extends BaseDataPo {

    @Column(name = "in_size_id")
    private String inSizeId;

    @Column(name = "in_size_name")
    private String inSizeName;

    @Column(name = "goods_id")
    private String goodsId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodsInSizePo that = (GoodsInSizePo) o;
        return Objects.equals(inSizeName, that.inSizeName) &&
                Objects.equals(goodsId, that.goodsId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(inSizeName, goodsId);
    }
}
