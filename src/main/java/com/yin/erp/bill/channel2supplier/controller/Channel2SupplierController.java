package com.yin.erp.bill.channel2supplier.controller;


import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.bill.channel2supplier.service.Channel2SupplierService;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 仓库出货
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/bill/channel2supplier")
public class Channel2SupplierController {

    @Autowired
    private Channel2SupplierService channel2SupplierService;
    @Autowired
    private LoginService userService;

    /**
     * 列表
     *
     * @return
     */
    @PostMapping(value = "list", consumes = "application/json")
    public BaseJson list(@RequestBody SearchBillVo vo) throws MessageException {
        return BaseJson.getSuccess(channel2SupplierService.findBillPage(vo));
    }

    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody BillVo vo, HttpServletRequest request) throws MessageException {
        channel2SupplierService.save(vo, userService.getUserSession(request));
        return BaseJson.getSuccess();
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) {
        channel2SupplierService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }


    /**
     * 审核
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "audit")
    public BaseJson audit(@RequestBody BaseAuditVo vo, HttpServletRequest request) throws MessageException {
        channel2SupplierService.audit(vo, userService.getUserSession(request));
        return BaseJson.getSuccess("审核成功");
    }

    /**
     * 反审核
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "un_audit")
    public BaseJson unAudit(@RequestBody BaseDeleteVo vo) throws MessageException {
        channel2SupplierService.unAudit(vo);
        return BaseJson.getSuccess("反审核成功");
    }


    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) throws MessageException {
        return BaseJson.getSuccess(channel2SupplierService.findById(id));
    }

    /**
     * 导出
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "export")
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        channel2SupplierService.export(vo, response);
    }

    /**
     * 上传
     *
     * @param file
     * @param request
     * @return
     */
    @PostMapping(value = "upload_bill")
    public BaseJson updateBill(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        channel2SupplierService.uploadBill(file, userService.getUserSession(request));
        return BaseJson.getSuccess("文件上传成功");
    }

}
