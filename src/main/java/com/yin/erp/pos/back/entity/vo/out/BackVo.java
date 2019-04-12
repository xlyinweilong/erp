package com.yin.erp.pos.back.entity.vo.out;

import com.yin.erp.pos.cash.entity.po.PosCashDetailPo;
import com.yin.erp.pos.cash.entity.po.PosCashPo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 退货VO
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class BackVo {

    /**
     * 单据详情
     */
    List<PosCashDetailPo> goodsList;

    /**
     * 单据
     */
    private PosCashPo posCashPo;

}
