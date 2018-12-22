package com.yin.erp.info.goods.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.goods.entity.vo.GoodsVo;
import com.yin.erp.info.goods.entity.vo.out.Goods4BillSearchVo;
import com.yin.erp.info.goods.service.GoodsService;
import com.yin.erp.user.service.UserService;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;


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
    public BaseJson list(GoodsVo vo) {
        return BaseJson.getSuccess(goodsService.findGoodsPage(vo));
    }

    /**
     * 单据查询货品
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list_by_bill")
    public BaseJson listByBill(GoodsVo vo) throws MessageException {
        Page<GoodsPo> page = goodsService.findGoodsPage(vo);
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
    public BaseJson logout(@RequestBody BaseDeleteVo vo) {
        goodsService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

    /**
     * 根据货号查询获得的颜色、内长、尺码列表
     *
     * @param id
     * @return
     */
    @GetMapping(value = "getGoodsColorAndInSizeAndSizeList")
    public BaseJson getGoodsColorAndInSizeAndSizeList(String id) {
        return BaseJson.getSuccess(goodsService.getGoodsColorAndInSizeAndSizeList(id));
    }

    /**
     * 导入货品资料
     *
     * @param file
     * @param request
     * @return
     */
    @PostMapping(value = "/upload_goods")
    public BaseJson updateGoods(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        goodsService.updateGoods(file, userService.getUserSession(request));
        return BaseJson.getSuccess("文件上传成功");
    }

    /**
     * 获取导入状态
     *
     * @param request
     * @return
     */
    @GetMapping(value = "upload_goods_status")
    public BaseJson uploadGoodsStatus(HttpServletRequest request) {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        return BaseJson.getSuccess(redisTemplate.opsForValue().get(userSessionBo.getId() + ":upload:goods"));
    }


    @GetMapping(value = "export")
    public void export(GoodsVo vo, HttpServletResponse response) throws Exception {
        vo.setPageIndex(1);
        vo.setPageSize(Integer.MAX_VALUE);
        Page<GoodsPo> goodsPage = goodsService.findGoodsPage(vo);
        List<GoodsPo> list = goodsPage.getContent();

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("信息表");

        //设置要导出的文件的名字
        String fileName = UUID.randomUUID().toString() + ".xls";
        //新增数据行，并且设置单元格数据

        int rowNum = 1;

        List<String> headers = Arrays.asList(new String[]{"货号", "名称", "尺码组", "品牌", "类别", "二级类别", "系列", "款型", "风格", "季节", "年份", "性别", "供应商编号", "吊牌价"});

        HSSFRow row = sheet.createRow(0);

        int i = 0;
        for (String header : headers) {
            HSSFCell cell = row.createCell(i++);
            HSSFRichTextString text = new HSSFRichTextString(header);
            cell.setCellValue(text);
        }

        for (GoodsPo goodsPo : list) {
            int j = 0;
            HSSFRow row1 = sheet.createRow(rowNum);
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
