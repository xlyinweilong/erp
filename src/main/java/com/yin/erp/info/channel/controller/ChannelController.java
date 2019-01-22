package com.yin.erp.info.channel.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.info.channel.entity.vo.ChannelVo;
import com.yin.erp.info.channel.service.ChannelService;
import com.yin.erp.user.user.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 渠道制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/info/channel")
public class ChannelController {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private LoginService loginService;


    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody ChannelVo vo) throws Exception {
        channelService.save(vo);
        return BaseJson.getSuccess();
    }

    /**
     * 列表
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(ChannelVo vo, HttpServletRequest request) {
        return BaseJson.getSuccess(channelService.findChannelPage(vo, loginService.getUserSession(request)));
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) {
        return BaseJson.getSuccess(channelService.findById(id));
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson logout(@RequestBody BaseDeleteVo vo) {
        channelService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

}
