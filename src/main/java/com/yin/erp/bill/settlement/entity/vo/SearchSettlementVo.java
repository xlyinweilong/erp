package com.yin.erp.bill.settlement.entity.vo;

import com.yin.erp.bill.common.entity.vo.BillVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 单据查询VO
 *
 * @author yin.weilong
 * @date 2018.12.02
 */
@Getter
@Setter
public class SearchSettlementVo extends BillVo {

    private String searchKey;

    private String type;

}
