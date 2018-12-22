package com.yin.erp.info.goods.feign;

import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.bo.GoodsBo;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 货品外服务
 *
 * @author yin.weilong
 * @date 2018.11.13
 */
@Service
public class GoodsFeign {

    @Autowired
    private GoodsDao goodsDao;

    public GoodsBo findGoodsBoById(String goodsId) throws MessageException {
        GoodsPo goodsPo = goodsDao.findById(goodsId).get();
        GoodsBo bo = new GoodsBo();
        CopyUtil.copyProperties(bo,goodsPo);
        return bo;
    }

}
