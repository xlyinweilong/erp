package com.yin.erp.info.marketpoint.dao;


import com.yin.erp.info.marketpoint.entity.po.MarketPointPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;

/**
 * 商场扣点
 *
 * @author yin
 */
@Resource
public interface MarketPointDao extends JpaRepository<MarketPointPo, String>, JpaSpecificationExecutor {

    MarketPointPo findByCode(String code);

}
