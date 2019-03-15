package com.yin.erp.bill.channel2channelout.controller;


import com.yin.common.controller.BaseJson;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.exceptions.MessageException;
import com.yin.common.service.LoginService;
import com.yin.erp.bill.channel2channelout.service.Channel2ChannelOutService;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
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
@RequestMapping(value = "api/bill/channel2channel_out")
public class Channel2ChannelOutController {

    @Autowired
    private Channel2ChannelOutService channel2ChannelOutService;
    @Autowired
    private LoginService userService;

    /**
     * 列表
     *
     * @return
     */
    @PostMapping(value = "list", consumes = "application/json")
    public BaseJson list(@RequestBody SearchBillVo vo) throws MessageException {
        return BaseJson.getSuccess(channel2ChannelOutService.findBillPage(vo));
    }

    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody BillVo vo, HttpServletRequest request) throws MessageException {
        channel2ChannelOutService.save(vo, userService.getUserSession(request));
        return BaseJson.getSuccess();
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws MessageException {
        channel2ChannelOutService.delete(vo);
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
        channel2ChannelOutService.audit(vo, userService.getUserSession(request));
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
        channel2ChannelOutService.unAudit(vo);
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
        return BaseJson.getSuccess(channel2ChannelOutService.findById(id));
    }

    /**
     * 导出
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "export")
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        channel2ChannelOutService.export(vo, response);
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
        channel2ChannelOutService.uploadBill(file, userService.getUserSession(request));
        return BaseJson.getSuccess("文件上传成功");
    }

    /**
     * 查询上级单据（渠道调出）
     *
     * @param code
     * @return
     */
    @GetMapping(value = "parent_bill")
    public BaseJson parentBill(String code) throws MessageException {
        return BaseJson.getSuccess(channel2ChannelOutService.findParentBill(code));
    }

    /**
     * 获取上级单据明细
     *
     * @param id
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "parent_bill_goods")
    public BaseJson parentBillGoods(String id) throws MessageException {
        return BaseJson.getSuccess(channel2ChannelOutService.findParentGoodsList(id));
    }

}
