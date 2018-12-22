package com.yin.erp.info.supplier.entity.vo;

import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 供应商VO
 *
 * @author yin
 */
@Getter
@Setter
public class SupplierVo extends BasePageVo {

    private String id;

    private String code;

    private String name;

    private String searchKey;

}
