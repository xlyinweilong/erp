package com.yin.erp.info.barcode.dao;


import com.yin.erp.info.barcode.entity.po.BarCodePo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Resource;

/**
 * 条形码
 *
 * @author yin
 */
@Resource
public interface BarCodeDao extends PagingAndSortingRepository<BarCodePo, String>, JpaSpecificationExecutor {

    BarCodePo findByCode(String code);
}
