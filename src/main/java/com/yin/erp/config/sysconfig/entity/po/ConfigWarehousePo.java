package com.yin.erp.config.sysconfig.entity.po;

import com.yin.common.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 仓库配置
 *
 * @author yin
 */
@Entity
@Table(name = "config_warehouse")
@Getter
@Setter
public class ConfigWarehousePo extends BaseDataPo {

    @Column(name = "config_id")
    private String configId;

    @Column(name = "warehouse_id")
    private String warehouseId;

    @Column(name = "default_value")
    private Integer defaultValue;

    public ConfigWarehousePo() {
    }

    public ConfigWarehousePo(String configId, String warehouseId, Integer defaultValue) {
        this.configId = configId;
        this.warehouseId = warehouseId;
        this.defaultValue = defaultValue;
    }
}
