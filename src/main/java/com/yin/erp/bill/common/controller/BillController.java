package com.yin.erp.bill.common.controller;


import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.out.BaseUploadMessage;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.info.dict.feign.DictFeign;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.dao.GoodsInSizeDao;
import com.yin.erp.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 单据通用
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/bill/common")
public class BillController {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private DictFeign dictFeign;
    @Autowired
    private GoodsColorDao goodsColorDao;
    @Autowired
    private GoodsInSizeDao goodsInSizeDao;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

//    /**
//     * 列表
//     *
//     * @return
//     */
//    @GetMapping(value = "goods_detail")
//    public BaseJson list(String goodsId) throws MessageException {
//        //查询货品
//        GoodsPo goodsPo = goodsDao.findById(goodsId).get();
//        //查询颜色
//        List<GoodsColorPo> colorList = goodsColorDao.findByGoodsId(goodsId);
//        //查询内长
//        List<GoodsInSizePo> inSizeList = goodsInSizeDao.findByGoodsId(goodsId);
//        //查询尺码组的尺码
//        List<DictSizeBo> sizeList = dictFeign.findDictSizePo(goodsPo.getSizeGroupId());
//        Map map = new HashMap();
//        map.put("colorList", colorList);
//        map.put("inSizeList", inSizeList);
//        map.put("sizeList", sizeList);
//        return BaseJson.getSuccess(map);
//    }

    /**
     * 获取导入状态
     *
     * @param request
     * @return
     */
    @GetMapping(value = "upload_status")
    public BaseJson uploadStatus(HttpServletRequest request) {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        return BaseJson.getSuccess(redisTemplate.opsForValue().get(userSessionBo.getId() + ":upload:bill"));
    }

    /**
     * 重置导入状态
     *
     * @param request
     * @return
     */
    @PostMapping(value = "reset_upload_status")
    public BaseJson resetUploadStatus(HttpServletRequest request) {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        redisTemplate.opsForValue().set(userSessionBo.getId() + ":upload:bill", new BaseUploadMessage(), 10L, TimeUnit.MINUTES);
        return BaseJson.getSuccess("操作成功");
    }

}
