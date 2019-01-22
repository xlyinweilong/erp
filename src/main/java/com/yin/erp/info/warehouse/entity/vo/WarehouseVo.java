package com.yin.erp.info.warehouse.entity.vo;

import com.yin.erp.base.entity.vo.in.BasePageVo;
import com.yin.erp.config.sysconfig.entity.po.ConfigWarehousePo;
import com.yin.erp.config.sysconfig.entity.po.ConfigPo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    private String groupId;

    private String groupName;


    /**
     * 传入配置
     */
    private List<ConfigPo> warehouseConfigList;

    /**
     * 传出配置
     */
    private List<ConfigWarehousePo> warehouseConfigWarehouseList;

}
