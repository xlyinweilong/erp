package com.yin.erp.info.dict.dao;


import com.yin.erp.info.dict.entity.po.DictPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.annotation.Resource;

/**
 * 字典
 *
 * @author yin
 */
@Resource
public interface DictDao extends JpaRepository<DictPo, String>, JpaSpecificationExecutor {

    DictPo findByCodeAndNameAndType1AndType2(String code, String name, String type1, String type2);

    DictPo findByNameAndType1AndType2(String name, String type1, String type2);

}
