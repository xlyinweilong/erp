package com.yin.erp.info.goods.dao;


import com.yin.erp.info.goods.entity.po.GoodsPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;

/**
 * 货品资料
 *
 * @author yin
 */
@Resource
public interface GoodsDao extends JpaRepository<GoodsPo, String>, JpaSpecificationExecutor {

    GoodsPo findByCode(String code);
}
