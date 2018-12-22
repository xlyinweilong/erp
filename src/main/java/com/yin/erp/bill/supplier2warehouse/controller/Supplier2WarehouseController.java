package com.yin.erp.bill.supplier2warehouse.controller;


import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseDetailDao;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseGoodsDao;
import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehouseDetailPo;
import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehouseGoodsPo;
import com.yin.erp.bill.supplier2warehouse.service.Supplier2WarehouseService;
import com.yin.erp.user.service.UserService;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 厂家来货
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/bill/supplier2warehouse")
public class Supplier2WarehouseController {

    @Autowired
    private Supplier2WarehouseService supplier2WarehouseService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private Supplier2WarehouseGoodsDao supplier2WarehouseGoodsDao;
    @Autowired
    private Supplier2WarehouseDetailDao supplier2WarehouseDetailDao;
    @Autowired
    private UserService userService;

    /**
     * 列表
     *
     * @return
     */
    @PostMapping(value = "list", consumes = "application/json")
    public BaseJson list(@RequestBody SearchBillVo vo) throws MessageException {
        return BaseJson.getSuccess(supplier2WarehouseService.findBillPage(vo));
    }

    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody BillVo vo, HttpServletRequest request) throws MessageException {
        supplier2WarehouseService.save(vo, userService.getUserSession(request));
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
        supplier2WarehouseService.delete(vo);
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
        supplier2WarehouseService.audit(vo, userService.getUserSession(request));
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
        supplier2WarehouseService.unAudit(vo);
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
        return BaseJson.getSuccess(supplier2WarehouseService.findById(id));
    }

    /**
     * 导出
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "export")
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        vo.setPageIndex(1);
        vo.setPageSize(Integer.MAX_VALUE);
        BackPageVo<BillVo> list = supplier2WarehouseService.findBillPage(vo);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("信息表");

        //设置要导出的文件的名字
        String fileName = UUID.randomUUID().toString() + ".xls";
        //新增数据行，并且设置单元格数据

        int rowNum = 1;


        List<String> headers = Arrays.asList(new String[]{"单据编号", "单据时间", "供应商编号", "供应商名称", "仓库编号", "仓库名称", "总数量", "总金额", "总吊牌价", "创建人", "审核人", "单据状态"});
        if ("BILL_GOODS".equals(vo.getType())) {
            headers = Arrays.asList(new String[]{"单据编号", "单据时间", "供应商编号", "供应商名称", "仓库编号", "仓库名称", "总数量", "总金额", "总吊牌价", "创建人", "审核人", "单据状态", "货号", "货品名称", "单价", "吊牌价", "数量", "金额", "吊牌额"});
        }
        if ("BILL_DETAIL".equals(vo.getType())) {
            headers = Arrays.asList(new String[]{"单据编号", "单据时间", "供应商编号", "供应商名称", "仓库编号", "仓库名称", "总数量", "总金额", "总吊牌价", "创建人", "审核人", "单据状态", "货号", "货品名称", "单价", "吊牌价", "颜色编号", "颜色名称", "内长", "尺码", "数量"});
        }
        HSSFRow row = sheet.createRow(0);
        int i = 0;
        for (String header : headers) {
            HSSFCell cell = row.createCell(i++);
            HSSFRichTextString text = new HSSFRichTextString(header);
            cell.setCellValue(text);
        }
        if ("BILL".equals(vo.getType())) {
            for (BillVo billVo : list.getContent()) {
                int j = 0;
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.createCell(j++).setCellValue(billVo.getCode());
                row1.createCell(j++).setCellValue(billVo.getBillDate().toString());
                row1.createCell(j++).setCellValue(billVo.getSupplierCode());
                row1.createCell(j++).setCellValue(billVo.getSupplierName());
                row1.createCell(j++).setCellValue(billVo.getWarehouseCode());
                row1.createCell(j++).setCellValue(billVo.getWarehouseName());
                row1.createCell(j++).setCellValue(billVo.getTotalCount());
                row1.createCell(j++).setCellValue(billVo.getTotalAmount().toPlainString());
                row1.createCell(j++).setCellValue(billVo.getTotalTagAmount().toPlainString());
                row1.createCell(j++).setCellValue(billVo.getCreateUserName());
                row1.createCell(j++).setCellValue(billVo.getAuditUserName());
                row1.createCell(j++).setCellValue(billVo.getStatusMean());
                rowNum++;
            }
        }
        if ("BILL_GOODS".equals(vo.getType())) {
            for (BillVo billVo : list.getContent()) {
                List<Supplier2WarehouseGoodsPo> goodsList = supplier2WarehouseGoodsDao.findByBillId(billVo.getId());
                for (Supplier2WarehouseGoodsPo goodsPo : goodsList) {
                    int j = 0;
                    HSSFRow row1 = sheet.createRow(rowNum);
                    row1.createCell(j++).setCellValue(billVo.getCode());
                    row1.createCell(j++).setCellValue(billVo.getBillDate().toString());
                    row1.createCell(j++).setCellValue(billVo.getSupplierCode());
                    row1.createCell(j++).setCellValue(billVo.getSupplierName());
                    row1.createCell(j++).setCellValue(billVo.getWarehouseCode());
                    row1.createCell(j++).setCellValue(billVo.getWarehouseName());
                    row1.createCell(j++).setCellValue(billVo.getTotalCount());
                    row1.createCell(j++).setCellValue(billVo.getTotalAmount().toPlainString());
                    row1.createCell(j++).setCellValue(billVo.getTotalTagAmount().toPlainString());
                    row1.createCell(j++).setCellValue(billVo.getCreateUserName());
                    row1.createCell(j++).setCellValue(billVo.getAuditUserName());
                    row1.createCell(j++).setCellValue(billVo.getStatusMean());

                    row1.createCell(j++).setCellValue(goodsPo.getGoodsCode());
                    row1.createCell(j++).setCellValue(goodsPo.getGoodsName());
                    row1.createCell(j++).setCellValue(goodsPo.getPrice().toPlainString());
                    row1.createCell(j++).setCellValue(goodsPo.getTagPrice().toPlainString());
                    row1.createCell(j++).setCellValue(goodsPo.getBillCount());
                    row1.createCell(j++).setCellValue(goodsPo.getPrice().multiply(BigDecimal.valueOf(goodsPo.getBillCount())).toPlainString());
                    row1.createCell(j++).setCellValue(goodsPo.getTagPrice().multiply(BigDecimal.valueOf(goodsPo.getBillCount())).toPlainString());
                    rowNum++;
                }
            }
        }
        if ("BILL_DETAIL".equals(vo.getType())) {
            for (BillVo billVo : list.getContent()) {
                List<Supplier2WarehouseDetailPo> detailList = supplier2WarehouseDetailDao.findByBillId(billVo.getId());
                for (Supplier2WarehouseDetailPo detailPo : detailList) {
                    int j = 0;
                    HSSFRow row1 = sheet.createRow(rowNum);
                    row1.createCell(j++).setCellValue(billVo.getCode());
                    row1.createCell(j++).setCellValue(billVo.getBillDate().toString());
                    row1.createCell(j++).setCellValue(billVo.getSupplierCode());
                    row1.createCell(j++).setCellValue(billVo.getSupplierName());
                    row1.createCell(j++).setCellValue(billVo.getWarehouseCode());
                    row1.createCell(j++).setCellValue(billVo.getWarehouseName());
                    row1.createCell(j++).setCellValue(billVo.getTotalCount());
                    row1.createCell(j++).setCellValue(billVo.getTotalAmount().toPlainString());
                    row1.createCell(j++).setCellValue(billVo.getTotalTagAmount().toPlainString());
                    row1.createCell(j++).setCellValue(billVo.getCreateUserName());
                    row1.createCell(j++).setCellValue(billVo.getAuditUserName());
                    row1.createCell(j++).setCellValue(billVo.getStatusMean());

                    row1.createCell(j++).setCellValue(detailPo.getGoodsCode());
                    row1.createCell(j++).setCellValue(detailPo.getGoodsName());
                    row1.createCell(j++).setCellValue(detailPo.getPrice().toPlainString());
                    row1.createCell(j++).setCellValue(detailPo.getTagPrice().toPlainString());

                    row1.createCell(j++).setCellValue(detailPo.getGoodsColorCode());
                    row1.createCell(j++).setCellValue(detailPo.getGoodsColorName());
                    row1.createCell(j++).setCellValue(detailPo.getGoodsInSizeName());
                    row1.createCell(j++).setCellValue(detailPo.getGoodsSizeName());
                    row1.createCell(j++).setCellValue(detailPo.getBillCount());

                    rowNum++;
                }

            }
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
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
        supplier2WarehouseService.uploadBill(file, userService.getUserSession(request));
        return BaseJson.getSuccess("文件上传成功");
    }


}
