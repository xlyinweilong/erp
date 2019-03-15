package com.yin.erp.info.barcode.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BaseUploadMessage;
import com.yin.common.exceptions.MessageException;
import com.yin.common.service.LoginService;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.info.barcode.entity.vo.BarCodeVo;
import com.yin.erp.info.barcode.service.BarCodeService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private LoginService userService;
    @Autowired
    private RedisTemplate redisTemplate;

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
     *
     * @param code
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "info_by_code")
    public BaseJson infoByCode(String code, HttpServletRequest request) throws MessageException {
        return BaseJson.getSuccess(barCodeService.findByCode(code, userService.getUserSession(request)));
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) {
        barCodeService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

    /**
     * 上传条形码
     *
     * @param file
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/upload")
    public BaseJson updateBarCode(@RequestParam("file") MultipartFile file, javax.servlet.http.HttpServletRequest request) throws Exception {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        ValueOperations operations = redisTemplate.opsForValue();
        LocalDateTime startTime = LocalDateTime.now();
        operations.set(userSessionBo.getId() + ":upload:barcode", new BaseUploadMessage(), 10L, TimeUnit.MINUTES);
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            barCodeService.updateBarCode(workbook, userSessionBo,startTime);
        } catch (Throwable e) {
            operations.set(userSessionBo.getId() + ":upload:barcode", new BaseUploadMessage(-1, TimeUtil.useTime(startTime), e.getMessage()), 10L, TimeUnit.MINUTES);
            e.printStackTrace();
        }
        return BaseJson.getSuccess("文件上传成功");
    }

}
