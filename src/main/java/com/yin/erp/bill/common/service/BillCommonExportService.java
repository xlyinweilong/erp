package com.yin.erp.bill.common.service;

import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.bill.common.dao.BaseBillDetailDao;
import com.yin.erp.bill.common.dao.BaseBillGoodsDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 单据导出服务
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class BillCommonExportService {

    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;


    /**
     * 单据导出
     *
     * @param vo
     * @param response
     * @param billService
     * @param baseBillGoodsDao
     * @param baseBillDetailDao
     * @param sourceKey
     * @param targetKey
     * @param hasParent
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response, BillService billService, BaseBillGoodsDao baseBillGoodsDao, BaseBillDetailDao baseBillDetailDao,
                       String sourceKey, String targetKey, boolean hasParent) throws Exception {
        vo.setPageIndex(1);
        vo.setPageSize(Integer.MAX_VALUE);
        BackPageVo<BillVo> list = billService.findBillPage(vo);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("信息表");

        //设置要导出的文件的名字
        String fileName = UUID.randomUUID().toString() + ".xls";
        //新增数据行，并且设置单元格数据

        int rowNum = 1;

        List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(new String[]{"单据编号", "单据时间"}));
        if (hasParent) {
            headers.add("上游单据单号");
        }
        this.addHeader(sourceKey, headers);
        this.addHeader(targetKey, headers);
        headers.addAll(Arrays.asList(new String[]{"总数量", "总金额", "总吊牌价", "创建人", "审核人", "单据状态"}));
        if ("BILL_GOODS".equals(vo.getType())) {
            headers.addAll(Arrays.asList(new String[]{"货号", "货品名称", "单价", "吊牌价", "数量", "金额", "吊牌额"}));
        }
        if ("BILL_DETAIL".equals(vo.getType())) {
            headers.addAll(Arrays.asList(new String[]{"货号", "货品名称", "单价", "吊牌价", "颜色编号", "颜色名称", "尺码", "数量"}));
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
                if (hasParent) {
                    row1.createCell(j++).setCellValue(billVo.getParentBillCode());
                }
                j = this.addData(sourceKey, j, row1, billVo);
                j = this.addData(targetKey, j, row1, billVo);
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
                List<BillGoodsPo> goodsList = baseBillGoodsDao.findByBillId(billVo.getId());
                for (BillGoodsPo goodsPo : goodsList) {
                    int j = 0;
                    HSSFRow row1 = sheet.createRow(rowNum);
                    row1.createCell(j++).setCellValue(billVo.getCode());
                    row1.createCell(j++).setCellValue(billVo.getBillDate().toString());
                    if (hasParent) {
                        row1.createCell(j++).setCellValue(billVo.getParentBillCode());
                    }
                    j = this.addData(sourceKey, j, row1, billVo);
                    j = this.addData(targetKey, j, row1, billVo);
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
                List<BillDetailPo> detailList = baseBillDetailDao.findByBillId(billVo.getId());
                for (BillDetailPo detailPo : detailList) {
                    int j = 0;
                    HSSFRow row1 = sheet.createRow(rowNum);
                    row1.createCell(j++).setCellValue(billVo.getCode());
                    row1.createCell(j++).setCellValue(billVo.getBillDate().toString());
                    if (hasParent) {
                        row1.createCell(j++).setCellValue(billVo.getParentBillCode());
                    }
                    j = this.addData(sourceKey, j, row1, billVo);
                    j = this.addData(targetKey, j, row1, billVo);
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
     * 添加header
     *
     * @param key
     * @param headers
     */
    private void addHeader(String key, List<String> headers) {
        if ("supplier".equals(key)) {
            headers.addAll(Arrays.asList(new String[]{"供应商编号", "供应商名称"}));
        } else if ("warehouse".equals(key)) {
            headers.addAll(Arrays.asList(new String[]{"仓库编号", "仓库名称"}));
        } else if ("channel".equals(key)) {
            headers.addAll(Arrays.asList(new String[]{"渠道编号", "渠道名称"}));
        }else if ("toChannel".equals(key)) {
            headers.addAll(Arrays.asList(new String[]{"入渠道编号", "入渠道名称"}));
        }
    }

    /**
     * 添加数据
     *
     * @param key
     * @param j
     * @param row1
     * @param billVo
     */
    private int addData(String key, int j, HSSFRow row1, BillVo billVo) {
        if ("supplier".equals(key)) {
            row1.createCell(j++).setCellValue(billVo.getSupplierCode());
            row1.createCell(j++).setCellValue(billVo.getSupplierName());
        } else if ("warehouse".equals(key)) {
            row1.createCell(j++).setCellValue(billVo.getWarehouseCode());
            row1.createCell(j++).setCellValue(billVo.getWarehouseName());
        } else if ("channel".equals(key)) {
            row1.createCell(j++).setCellValue(billVo.getChannelCode());
            row1.createCell(j++).setCellValue(billVo.getChannelName());
        }else if ("toChannel".equals(key)) {
            row1.createCell(j++).setCellValue(billVo.getToChannelCode());
            row1.createCell(j++).setCellValue(billVo.getToChannelName());
        }
        return j;
    }

}
