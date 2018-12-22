package com.yin.erp.info.supplier.feign;

import com.yin.erp.info.supplier.dao.SupplierDao;
import com.yin.erp.info.supplier.entity.po.SupplierPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 供应商对外服务
 *
 * @author yin.weilong
 * @date 2018.11.13
 */
@Service
public class SupplierFeign {

    @Autowired
    private SupplierDao supplierDao;

    /**
     * 通过ID获取名称
     *
     * @param id
     * @return
     */
    public String getNameById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        Optional<SupplierPo> optional = supplierDao.findById(id);
        return optional.isPresent() ? optional.get().getName() : null;
    }

    /**
     * 通过ID获取编号
     *
     * @param id
     * @return
     */
    public String getCodeById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        Optional<SupplierPo> optional = supplierDao.findById(id);
        return optional.isPresent() ? optional.get().getCode() : null;
    }
}
