package com.yin.erp.info.supplier.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.info.supplier.entity.vo.SupplierVo;
import com.yin.erp.info.supplier.service.SupplierService;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 供应商控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/info/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;
    @Autowired
    private LoginService loginService;


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
    public BaseJson list(SupplierVo vo, HttpServletRequest request) {
        return BaseJson.getSuccess(supplierService.findSupplierPage(vo, loginService.getUserSession(request)));
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
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws Exception{
        supplierService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

}
