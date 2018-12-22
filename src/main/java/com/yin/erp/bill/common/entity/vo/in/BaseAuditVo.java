package com.yin.erp.bill.common.entity.vo.in;

import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseAuditVo extends BaseDeleteVo {
    
    private String status;
}
