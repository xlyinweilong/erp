package com.yin.erp.info.dict.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.info.dict.entity.vo.DictVo;
import com.yin.erp.info.dict.entity.vo.in.DictDeleteVo;
import com.yin.erp.info.dict.enums.*;
import com.yin.erp.info.dict.service.DictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 字典控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/info/dict")
public class DictController {

    @Autowired
    private DictService dictService;


    /**
     * 获取字典列表
     *
     * @param type
     * @return
     */
    @GetMapping(value = "dict_list")
    public BaseJson dictTypeList(String type) throws Exception {
        if (StringUtils.isBlank(type)) {
            return BaseJson.getSuccess(DictType.getMeanList());
        } else {
            switch (DictType.valueOf(type)) {
                case GOODS:
                    return BaseJson.getSuccess(DictGoodsType.getMeanList());
                case CHANNEL:
                    return BaseJson.getSuccess(DictChannelType.getMeanList());
                case WAREHOUSE:
                    return BaseJson.getSuccess(DictWarehouseType.getMeanList());
                case SUPPLIER:
                    return BaseJson.getSuccess(DictSupplierType.getMeanList());
                default:
                    throw new MessageException("不存在的字典");
            }
        }
    }

    /**
     * 保存
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody DictVo vo) throws Exception {
        dictService.save(vo);
        return BaseJson.getSuccess();
    }

    /**
     * 列表
     *
     * @param vo
     * @return
     */
    @GetMapping(value = "list")
    public BaseJson list(DictVo vo) {
        if (DictGoodsType.SIZE_GROUP.name().equals(vo.getType2())) {
            return BaseJson.getSuccess(dictService.findSizeGroupList(vo));
        } else {
            return BaseJson.getSuccess(dictService.findDictPage(vo));
        }
    }

    /**
     * 详情
     *
     * @param id
     * @return
     */
    @GetMapping(value = "info")
    public BaseJson info(String id) {
        return BaseJson.getSuccess(dictService.findById(id));
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody DictDeleteVo vo) throws MessageException {
        dictService.delete(vo);
        return BaseJson.getSuccess("删除成功");
    }

}
