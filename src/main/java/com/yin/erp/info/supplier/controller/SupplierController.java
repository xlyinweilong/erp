package com.yin.erp.info.supplier.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.info.supplier.entity.vo.SupplierVo;
import com.yin.erp.info.supplier.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "info/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;


    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody SupplierVo vo) throws Exception {
        supplierService.save(vo);
        return BaseJson.getSuccess();
    }

    /**
     * 列表
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(SupplierVo vo) {
        return BaseJson.getSuccess(supplierService.findDictPage(vo));
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) {
        return BaseJson.getSuccess(supplierService.findById(id));
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson logout(@RequestBody BaseDeleteVo vo) {
        supplierService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

}
