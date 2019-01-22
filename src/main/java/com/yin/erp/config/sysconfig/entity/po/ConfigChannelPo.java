package com.yin.erp.config.sysconfig.entity.po;

import com.yin.erp.base.entity.po.BaseDataPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 渠道配置
 *
 * @author yin
 */
@Entity
@Table(name = "config_channel")
@Getter
@Setter
public class ConfigChannelPo extends BaseDataPo {

    @Column(name = "config_id")
    private String configId;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "default_value")
    private Integer defaultValue;

    public ConfigChannelPo() {
    }

    public ConfigChannelPo(String configId, String channelId, Integer defaultValue) {
        this.configId = configId;
        this.channelId = channelId;
        this.defaultValue = defaultValue;
    }
}
