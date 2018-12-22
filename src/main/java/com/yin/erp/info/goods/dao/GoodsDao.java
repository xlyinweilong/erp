package com.yin.erp.info.goods.dao;


import com.yin.erp.info.goods.entity.po.GoodsPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.annotation.Resource;

/**
 * 货品资料
 *
 * @author yin
 */
@Resource
public interface GoodsDao extends PagingAndSortingRepository<GoodsPo, String>, JpaSpecificationExecutor {

    GoodsPo findByCode(String code);
}
