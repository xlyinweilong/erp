package com.yin.erp.bill.common.service;

import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;

/**
 * 单据服务
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
public class BillService implements BillServiceInterface {

    public BackPageVo<BillVo> findBillPage(SearchBillVo vo) throws MessageException {
        return null;
    }

    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return null;
    }

    public void audit(BaseAuditVo vo, UserSessionBo userSessionBo) throws MessageException {
    }

}
