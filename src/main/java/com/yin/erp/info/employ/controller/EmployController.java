package com.yin.erp.info.employ.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.info.employ.entity.vo.EmployVo;
import com.yin.erp.info.employ.service.EmployService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 员工制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/info/employ")
public class EmployController {

    @Autowired
    private EmployService employService;


    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody EmployVo vo) throws Exception {
        employService.save(vo);
        return BaseJson.getSuccess();
    }

    /**
     * 列表
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(EmployVo vo) {
        return BaseJson.getSuccess(employService.findDictPage(vo));
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) {
        return BaseJson.getSuccess(employService.findById(id));
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws Exception {
        employService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

}
