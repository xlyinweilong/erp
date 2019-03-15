package com.yin.erp.pos.cash.entity.vo.out;

import com.yin.common.entity.vo.BaseVo;
import com.yin.erp.pos.cash.entity.po.PosCashDetailPo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

/**
 * 查询返回总计VO
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class PosSearchOutTotalVo extends BaseVo {

    private BigDecimal totalAmount;

    private Long totalCount;

    private Page<PosCashDetailPo> page;

    public PosSearchOutTotalVo() {
    }

    public PosSearchOutTotalVo(BigDecimal totalAmount, Long totalCount) {
        this.totalAmount = totalAmount;
        this.totalCount = totalCount;
    }
}
