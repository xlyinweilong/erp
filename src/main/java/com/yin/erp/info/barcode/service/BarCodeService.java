package com.yin.erp.info.barcode.service;

import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.info.barcode.dao.BarCodeDao;
import com.yin.erp.info.barcode.entity.po.BarCodePo;
import com.yin.erp.info.barcode.entity.vo.BarCodeVo;
import com.yin.erp.info.dict.feign.DictFeign;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.bo.GoodsBo;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.goods.feign.GoodsFeign;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 条形码服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class BarCodeService {

    @Autowired
    private BarCodeDao barCodeDao;
    @Autowired
    private GoodsFeign goodsFeign;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private DictFeign dictFeign;

    /**
     * 保存
     *
     * @param vo
     * @throws Exception
     */
    public void save(BarCodeVo vo) throws MessageException {
        BarCodePo po = new BarCodePo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = barCodeDao.findById(vo.getId()).get();
            //发送给队列，全局做数据更新 TODO
        }
        po.setCode(vo.getCode());
        po.setGoodsId(vo.getGoodsId());
        GoodsBo bo = goodsFeign.findGoodsBoById(vo.getGoodsId());
        po.setGoodsCode(bo.getCode());
        po.setGoodsName(bo.getName());
        po.setGoodsColorId(vo.getGoodsColorId());
        po.setGoodsColorCode(dictFeign.getCodeById(vo.getGoodsColorId()));
        po.setGoodsColorName(dictFeign.getCodeById(vo.getGoodsColorId()));
        po.setGoodsInSizeId(vo.getGoodsInSizeId());
        po.setGoodsInSizeName(dictFeign.getNameById(vo.getGoodsInSizeId()));
        po.setGoodsSizeId(vo.getGoodsSizeId());
        po.setGoodsSizeName(dictFeign.findSizeNamenById(vo.getGoodsSizeId()));
        barCodeDao.save(po);
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BarCodeVo findById(String id) throws MessageException {
        BarCodePo dictPo = barCodeDao.findById(id).get();
        BarCodeVo dictVo = new BarCodeVo();
        CopyUtil.copyProperties(dictVo, dictPo);
        dictVo.setGoodsCode(dictPo.getGoodsCode());
        return dictVo;
    }

    /**
     * 根据code查询
     *
     * @param code
     * @return
     * @throws MessageException
     */
    public BarCodeVo findByCode(String code) throws MessageException {
        BarCodePo dictPo = barCodeDao.findByCode(code);
        if (dictPo == null) {
            throw new MessageException("不存在条形码：" + code);
        }
        BarCodeVo dictVo = new BarCodeVo();
        CopyUtil.copyProperties(dictVo, dictPo);
        dictVo.setGoodsCode(dictPo.getGoodsCode());
        dictVo.setGoodsName(dictPo.getGoodsName());
        //查询价格
        GoodsPo goodsPo = goodsDao.findByCode(dictPo.getGoodsCode());
        dictVo.setPrice(goodsPo.getTagPrice1());
        dictVo.setTagPrice(goodsPo.getTagPrice1());
        return dictVo;
    }

    /**
     * 查询字典
     *
     * @param vo
     * @return
     */
    public Page<BarCodePo> findDictPage(BarCodeVo vo) {
        Page<BarCodePo> page = barCodeDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getGoodsCode())) {
                predicates.add(criteriaBuilder.like(root.get("goodsCode"), "%" + vo.getGoodsCode() + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        return page;
    }

    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            //查询货品/渠道引用情况 TODO
            barCodeDao.deleteById(id);
        }
    }


}
