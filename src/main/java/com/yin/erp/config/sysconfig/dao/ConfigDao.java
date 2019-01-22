package com.yin.erp.config.sysconfig.dao;


import com.yin.erp.config.sysconfig.entity.po.ConfigPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;

/**
 * 配置
 *
 * @author yin
 */
@Resource
public interface ConfigDao extends JpaRepository<ConfigPo, String>, JpaSpecificationExecutor {
}
