package com.yin.erp.bill.common.entity.vo.in;

import com.yin.erp.bill.common.entity.vo.BillVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 单据查询VO
 *
 * @author yin.weilong
 * @date 2018.12.02
 */
@Getter
@Setter
public class SearchBillVo extends BillVo {

    private String searchKey;

    private String startBillDate;

    private String endBillDate;

    private List<String> statusList;
}
