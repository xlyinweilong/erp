package com.yin.erp.info.marketpoint.service;

import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.info.marketpoint.dao.MarketPointDao;
import com.yin.erp.info.marketpoint.entity.po.MarketPointPo;
import com.yin.erp.info.marketpoint.entity.vo.MarketPointVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商场扣点
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class MarketPointService {

    @Autowired
    private MarketPointDao marketPointDao;

    /**
     * 保存
     *
     * @param vo
     * @throws Exception
     */
    public void save(MarketPointVo vo, UserSessionBo user) throws MessageException {
        MarketPointPo po = new MarketPointPo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = marketPointDao.findById(vo.getId()).get();
            po.setUpdateUserId(user.getId());
        } else {
            po.setCreateUserId(user.getId());
        }
        po.setPoint(vo.getPoint());
        po.setCode(vo.getCode());
        po.setName(vo.getName());
        po.setRemarks(vo.getRemarks());
        marketPointDao.save(po);
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public MarketPointVo findById(String id) throws MessageException {
        MarketPointPo marketPointPo = marketPointDao.findById(id).get();
        MarketPointVo marketPointVo = new MarketPointVo();
        CopyUtil.copyProperties(marketPointVo, marketPointPo);
        return marketPointVo;
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            //查询是否被商场引用 todo
            marketPointDao.deleteById(id);
        }
    }


}
