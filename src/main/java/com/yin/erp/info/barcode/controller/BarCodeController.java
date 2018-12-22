package com.yin.erp.info.barcode.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.info.barcode.entity.vo.BarCodeVo;
import com.yin.erp.info.barcode.service.BarCodeService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 条形码制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/info/barCode")
public class BarCodeController {

    @Autowired
    private BarCodeService barCodeService;


    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody BarCodeVo vo) throws Exception {
        barCodeService.save(vo);
        return BaseJson.getSuccess();
    }

    /**
     * 列表
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(BarCodeVo vo, HttpServletRequest request) {
        return BaseJson.getSuccess(barCodeService.findDictPage(vo));
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) throws MessageException {
        return BaseJson.getSuccess(barCodeService.findById(id));
    }

    /**
     * 详情
     * @param code
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "info_by_code")
    public BaseJson infoByCode(String code) throws MessageException {
        return BaseJson.getSuccess(barCodeService.findByCode(code));
    }


    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson logout(@RequestBody BaseDeleteVo vo) {
        barCodeService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

}
