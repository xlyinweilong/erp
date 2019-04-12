package com.yin.erp.pos.back.entity.vo.in;

import com.yin.erp.pos.cash.entity.po.PosCashDetailPo;
import lombok.Getter;
import lombok.Setter;

/**
 * 退货VO
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class BackGoodsVo extends PosCashDetailPo {


    /**
     * 退回数量
     */
    private Integer backCount;

}
