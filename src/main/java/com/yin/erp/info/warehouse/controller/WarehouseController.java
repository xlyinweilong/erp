package com.yin.erp.info.warehouse.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.service.LoginService;
import com.yin.erp.info.warehouse.entity.vo.WarehouseVo;
import com.yin.erp.info.warehouse.service.WarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 仓库制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/info/warehouse")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private LoginService loginService;


    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody WarehouseVo vo) throws Exception {
        warehouseService.save(vo);
        return BaseJson.getSuccess();
    }

    /**
     * 列表
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(WarehouseVo vo ,HttpServletRequest request) {
        return BaseJson.getSuccess(warehouseService.findWarehousePage(vo,loginService.getUserSession(request)));
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) {
        return BaseJson.getSuccess(warehouseService.findById(id));
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public BaseJson logout(@RequestBody BaseDeleteVo vo) throws Exception{
        warehouseService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

}
