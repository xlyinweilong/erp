package com.yin.erp.info.warehouse.entity.vo;

import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 仓库VO
 *
 * @author yin
 */
@Getter
@Setter
public class WarehouseVo extends BasePageVo {

    private String id;

    private String code;

    private String name;

}
