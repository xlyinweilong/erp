package com.yin.erp.info.goods.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.entity.vo.out.BaseUploadMessage;
import com.yin.common.exceptions.MessageException;
import com.yin.common.service.LoginService;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.goods.entity.vo.GoodsVo;
import com.yin.erp.info.goods.entity.vo.out.Goods4BillSearchVo;
import com.yin.erp.info.goods.service.GoodsService;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 货品资料制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/info/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    @Autowired
    private LoginService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LoginService loginService;


    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody GoodsVo vo) throws Exception {
        goodsService.save(vo);
        return BaseJson.getSuccess();
    }

    /**
     * 列表
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(GoodsVo vo, HttpServletRequest request) {
        return BaseJson.getSuccess(goodsService.findGoodsPage(vo, loginService.getUserSession(request)));
    }

    /**
     * 单据查询货品
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list_by_bill")
    public BaseJson listByBill(GoodsVo vo, HttpServletRequest request) throws MessageException {
        Page<GoodsPo> page = goodsService.findGoodsPage(vo, loginService.getUserSession(request));
        BackPageVo backPageVo = new BackPageVo();
        backPageVo.setTotalElements(page.getTotalElements());
        List<Goods4BillSearchVo> content = new LinkedList<>();
        for (GoodsPo po : page.getContent()) {
            Goods4BillSearchVo goodsVo = new Goods4BillSearchVo();
            goodsVo.setTagPrice(po.getTagPrice1());
            goodsVo.setPrice(po.getTagPrice1());
            CopyUtil.copyProperties(goodsVo, po);
            content.add(goodsVo);
        }
        backPageVo.setContent(content);
        return BaseJson.getSuccess(backPageVo);
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) {
        return BaseJson.getSuccess(goodsService.findById(id));
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws Exception {
        goodsService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

    /**
     * 根据货号查询获得的颜色、内长、尺码列表
     *
     * @param id
     * @return
     */
    @GetMapping(value = "getGoodsColorAndSizeList")
    public BaseJson getGoodsColorAndSizeList(String id) {
        return BaseJson.getSuccess(goodsService.getGoodsColorAndSizeList(id));
    }

    /**
     * 导入货品资料
     *
     * @param file
     * @param request
     * @return
     */
    @PostMapping(value = "/upload_goods")
    public BaseJson updateGoods(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws Exception {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        ValueOperations operations = redisTemplate.opsForValue();
        LocalDateTime startTime = LocalDateTime.now();
        operations.set(userSessionBo.getId() + ":upload:goods", new BaseUploadMessage(), 10L, TimeUnit.MINUTES);
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            goodsService.updateGoods(workbook, userSessionBo, startTime);
        } catch (Throwable e) {
            operations.set(userSessionBo.getId() + ":upload:goods", new BaseUploadMessage(-1, TimeUtil.useTime(startTime), e.getMessage()), 10L, TimeUnit.MINUTES);
            e.printStackTrace();
        }
        return BaseJson.getSuccess("文件上传成功");
    }

    @GetMapping(value = "export")
    public void export(GoodsVo vo, HttpServletRequest request, HttpServletResponse response) throws Exception {
        vo.setPageIndex(1);
        vo.setPageSize(Integer.MAX_VALUE);
        Page<GoodsPo> goodsPage = goodsService.findGoodsPage(vo, loginService.getUserSession(request));
        List<GoodsPo> list = goodsPage.getContent();

        SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
        SXSSFSheet sheet = workbook.createSheet("信息表");

        //设置要导出的文件的名字
        String fileName = UUID.randomUUID().toString() + ".xls";
        //新增数据行，并且设置单元格数据

        int rowNum = 1;

        List<String> headers = Arrays.asList(new String[]{"货号", "名称", "尺码组", "品牌", "类别", "二级类别", "系列", "款型", "风格", "季节", "年份", "性别", "供应商编号", "吊牌价"});

        SXSSFRow row = sheet.createRow(0);

        int i = 0;
        for (String header : headers) {
            SXSSFCell cell = row.createCell(i++);
            HSSFRichTextString text = new HSSFRichTextString(header);
            cell.setCellValue(text);
        }

        for (GoodsPo goodsPo : list) {
            int j = 0;
            SXSSFRow row1 = sheet.createRow(rowNum);
            row1.createCell(j++).setCellValue(goodsPo.getCode());
            row1.createCell(j++).setCellValue(goodsPo.getName());
            row1.createCell(j++).setCellValue(goodsPo.getSizeGroupName());
            row1.createCell(j++).setCellValue(goodsPo.getBrandName());
            row1.createCell(j++).setCellValue(goodsPo.getCategoryName());
            row1.createCell(j++).setCellValue(goodsPo.getCategory2Name());
            row1.createCell(j++).setCellValue(goodsPo.getSeriesName());
            row1.createCell(j++).setCellValue(goodsPo.getPatternName());
            row1.createCell(j++).setCellValue(goodsPo.getStyleName());
            row1.createCell(j++).setCellValue(goodsPo.getSeasonName());
            row1.createCell(j++).setCellValue(goodsPo.getYearName());
            row1.createCell(j++).setCellValue(goodsPo.getSexName());
            row1.createCell(j++).setCellValue(goodsPo.getSupplierCode());
            row1.createCell(j++).setCellValue(goodsPo.getTagPrice1().toPlainString());
            rowNum++;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }

}
