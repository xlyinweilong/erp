package com.yin.erp.info.goods.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

/**
 * 货品颜色资料
 *
 * @author yin
 */
@Entity
@Table(name = "goods_color")
@Getter
@Setter
public class GoodsColorPo extends BaseDataPo {

    @Column(name = "color_id")
    private String colorId;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "color_name")
    private String colorName;

    @Column(name = "goods_id")
    private String goodsId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoodsColorPo that = (GoodsColorPo) o;
        return Objects.equals(colorCode, that.colorCode) &&
                Objects.equals(colorName, that.colorName) &&
                Objects.equals(goodsId, that.goodsId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(colorCode, colorName, goodsId);
    }
}
