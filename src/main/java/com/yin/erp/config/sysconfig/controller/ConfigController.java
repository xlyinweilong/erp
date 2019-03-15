package com.yin.erp.config.sysconfig.controller;

import com.yin.common.controller.BaseJson;
import com.yin.erp.config.sysconfig.dao.ConfigDao;
import com.yin.erp.config.sysconfig.entity.po.ConfigPo;
import com.yin.erp.config.sysconfig.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 配置制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/info/config")
public class ConfigController {

    @Autowired
    private ConfigDao configDao;
    @Autowired
    private ConfigService configService;



    /**
     * 保存
     *
     * @param list
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody List<ConfigPo> list) throws Exception {
        configService.save(list);
        return BaseJson.getSuccess();
    }

    /**
     * 列表
     *
     * @return
     */
    @GetMapping(value = "all")
    public BaseJson list(HttpServletRequest request) {
        return BaseJson.getSuccess(configDao.findAll());
    }


}
