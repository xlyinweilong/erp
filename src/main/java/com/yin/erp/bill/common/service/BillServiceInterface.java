package com.yin.erp.bill.common.service;

import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;

/**
 * 单据服务
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
public interface BillServiceInterface {

    /**
     * 保存单据
     *
     * @param vo
     * @param userSessionBo
     * @throws MessageException
     */
    BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException;


}
