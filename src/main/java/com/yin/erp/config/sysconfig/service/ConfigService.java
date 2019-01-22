package com.yin.erp.config.sysconfig.service;

import com.yin.erp.config.sysconfig.dao.ConfigChannelDao;
import com.yin.erp.config.sysconfig.dao.ConfigDao;
import com.yin.erp.config.sysconfig.dao.ConfigWarehouseDao;
import com.yin.erp.config.sysconfig.entity.po.ConfigChannelPo;
import com.yin.erp.config.sysconfig.entity.po.ConfigPo;
import com.yin.erp.config.sysconfig.entity.po.ConfigWarehousePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 配置服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ConfigService {

    @Autowired
    private ConfigDao configDao;
    @Autowired
    private ConfigChannelDao configChannelDao;
    @Autowired
    private ConfigWarehouseDao configWarehouseDao;

    /**
     * 获取系统配置
     *
     * @param configId
     * @return
     */
    public int getSysConfigValue(String configId) {
        ConfigPo configPo = configDao.findById(configId).get();
        return configPo.getDefaultValue();
    }

    /**
     * 获取渠道配置
     *
     * @param configId
     * @param channelId
     * @return
     */
    public int getChannelConfigValue(String configId, String channelId) {
        ConfigChannelPo configChannelPo = configChannelDao.findByChannelIdAndConfigId(channelId, configId);
        ConfigPo configPo = configDao.findById(configId).get();
        if (configChannelPo != null) {
            if (configChannelPo.getDefaultValue() == -1) {
                return configPo.getDefaultValue();
            }
            return configChannelPo.getDefaultValue();
        }
        return configPo.getDefaultValue();
    }

    /**
     * 获取仓库配置
     *
     * @param configId
     * @param warehouseId
     * @return
     */
    public int getWarehouseConfigValue(String configId, String warehouseId) {
        ConfigWarehousePo configWarehousePo = configWarehouseDao.findByWarehouseIdAndConfigId(warehouseId, configId);
        ConfigPo configPo = configDao.findById(configId).get();
        if (configWarehousePo != null) {
            if (configWarehousePo.getDefaultValue() == -1) {
                return configPo.getDefaultValue();
            }
            return configWarehousePo.getDefaultValue();
        }
        return configPo.getDefaultValue();
    }

    /**
     * 保存
     *
     * @param list
     */
    public void save(List<ConfigPo> list) {
        configDao.saveAll(list);
    }


}
