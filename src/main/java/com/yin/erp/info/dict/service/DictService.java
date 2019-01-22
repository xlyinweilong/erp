package com.yin.erp.info.dict.service;

import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.po.DictPo;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.dict.entity.vo.DictVo;
import com.yin.erp.info.dict.entity.vo.in.DictDeleteVo;
import com.yin.erp.info.dict.enums.DictGoodsType;
import com.yin.erp.info.dict.enums.DictType;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.entity.vo.GoodsVo;
import com.yin.erp.info.goods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 字典服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class DictService {

    @Autowired
    private DictDao dictDao;
    @Autowired
    private DictSizeDao dictSizeDao;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private GoodsColorDao goodsColorDao;


    /**
     * 保存
     *
     * @param vo
     * @throws Exception
     */
    public void save(DictVo vo) throws MessageException {
        DictPo po = new DictPo();
        if (StringUtils.isNotBlank(vo.getId())) {
            //判断引用情况
            if (!DictGoodsType.SIZE_GROUP.name().equals(po.getType2()) && this.isQuote(vo.getId(), vo.getType1(), vo.getType2())) {
                DictPo p = dictDao.findById(vo.getId()).get();
                throw new MessageException((StringUtils.isNotBlank(p.getCode()) ? "编号：" + p.getCode() + "，" : "") + "名称：" + p.getName() + "，已经被引用，不能修改");
            }
            po = dictDao.findById(vo.getId()).get();
        }
        String oldName = po.getName();
        po.setType1(vo.getType1());
        po.setType2(vo.getType2());
        po.setCode(vo.getCode());
        po.setName(vo.getName());
        dictDao.save(po);
        if (DictGoodsType.SIZE_GROUP.name().equals(po.getType2())) {
            List<DictSizePo> dictSizeList = dictSizeDao.findByGroupId(po.getId());
            //判断引用情况
            if (StringUtils.isNotBlank(vo.getId()) && this.isQuote(vo.getId(), vo.getType1(), vo.getType2())) {
                if (!oldName.equals(vo.getName())) {
                    throw new MessageException("尺码组：" + oldName + "，已经被引用，不能名称修改");
                }
                //判断尺码的引用情况（单据、条形码）
                List<DictSizePo> deleteDictSizeList = dictSizeList.stream().filter(d -> !vo.getSizeList().contains(d.getId())).collect(Collectors.toList());
            }
            dictSizeDao.deleteByGroupId(po.getId());
            if (vo.getSizeList() != null) {
                Set setName = new HashSet<>();
                for (int i = 0; i < vo.getSizeList().size(); i++) {
                    DictSizePo voData = vo.getSizeList().get(i);
                    DictSizePo dictSizePo = new DictSizePo();
                    dictSizePo.setId(voData.getId());
                    dictSizePo.setOrderIndex(i);
                    dictSizePo.setGroupId(po.getId());
                    dictSizePo.setGroupName(po.getName());
                    dictSizePo.setName(voData.getName());
                    dictSizeDao.save(dictSizePo);
                    setName.add(dictSizePo.getName().toUpperCase());
                }
                if (setName.size() != vo.getSizeList().size()) {
                    throw new MessageException("尺码存在重复");
                }
            }
        }

    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public DictVo findById(String id) {
        DictPo dictPo = dictDao.findById(id).get();
        DictVo dictVo = new DictVo();
        dictVo.setId(dictPo.getId());
        dictVo.setCode(dictPo.getCode());
        dictVo.setName(dictPo.getName());
        dictVo.setType1(dictPo.getType1());
        dictVo.setType2(dictPo.getType2());
        if (DictGoodsType.SIZE_GROUP.name().equals(dictPo.getType2())) {
            dictVo.setSizeList(dictSizeDao.findByGroupId(dictPo.getId()));
        }
        return dictVo;
    }

    /**
     * 查询尺码组
     *
     * @param vo
     * @return
     */
    public BackPageVo<DictVo> findSizeGroupList(DictVo vo) {
        BackPageVo backPageVo = new BackPageVo();
        Page<DictPo> page = this.findDictPage(vo);
        backPageVo.setTotalElements(page.getTotalElements());
        List<DictVo> list = new ArrayList<>();
        for (DictPo dictPo : page.getContent()) {
            DictVo dictVo = new DictVo();
            dictVo.setId(dictPo.getId());
            dictVo.setName(dictPo.getName());
            dictVo.setType1(dictPo.getType1());
            dictVo.setType2(dictPo.getType2());
            dictVo.setSizeList(dictSizeDao.findByGroupId(dictPo.getId()));
            list.add(dictVo);
        }
        backPageVo.setContent(list);
        return backPageVo;
    }

    /**
     * 查询字典
     *
     * @param vo
     * @return
     */
    public Page<DictPo> findDictPage(DictVo vo) {
        Sort sort = new Sort(Sort.Direction.ASC, "orderIndex").and(new Sort(Sort.Direction.DESC, "createDate"));
        Page<DictPo> page = dictDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("type1"), vo.getType1()));
            predicates.add(criteriaBuilder.equal(root.get("type2"), vo.getType2()));
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSearchKey())) {
                Predicate p1 = criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%");
                Predicate p2 = criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%");
                Predicate predicatesPermission = criteriaBuilder.or(p1, p2);
                predicates.add(predicatesPermission);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), sort));
        return page;
    }

    /**
     * 删除
     *
     * @param vo
     */
    public void delete(DictDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            //查询货品是否引用
            if (DictGoodsType.SIZE_GROUP.name().equals(vo.getType2())) {
                dictSizeDao.deleteByGroupId(id);
            }
            if (this.isQuote(id, vo.getType1(), vo.getType2())) {
                DictPo po = dictDao.findById(id).get();
                throw new MessageException((StringUtils.isNotBlank(po.getCode()) ? "编号：" + po.getCode() + "，" : "") + "名称：" + po.getName() + "，已经被引用，不能删除");
            }
            dictDao.deleteById(id);
        }
    }

    /**
     * 判断是否被引用
     *
     * @param id
     * @param type1
     * @param type2
     * @throws MessageException
     */
    private boolean isQuote(String id, String type1, String type2) {
        if (DictType.GOODS.name().equals(type1)) {
            if (DictGoodsType.COLOR.name().equals(type2)) {
                if (goodsColorDao.countByColorId(id) > 0L) {
                    return true;
                }
            } else {
                GoodsVo goodsVo = new GoodsVo();
                if (DictGoodsType.SIZE_GROUP.name().equals(type2)) {
                    goodsVo.setSizeGroupId(id);
                }
                if (DictGoodsType.BRAND.name().equals(type2)) {
                    goodsVo.setBrandId(id);
                }
                if (DictGoodsType.CATEGORY.name().equals(type2)) {
                    goodsVo.setCategoryId(id);
                }
                if (DictGoodsType.CATEGORY_2.name().equals(type2)) {
                    goodsVo.setCategory2Id(id);
                }
                if (DictGoodsType.SERIES.name().equals(type2)) {
                    goodsVo.setSeriesId(id);
                }
                if (DictGoodsType.PATTERN.name().equals(type2)) {
                    goodsVo.setPatternId(id);
                }
                if (DictGoodsType.STYLE.name().equals(type2)) {
                    goodsVo.setStyleId(id);
                }
                if (DictGoodsType.SEASON.name().equals(type2)) {
                    goodsVo.setSeasonId(id);
                }
                if (DictGoodsType.YEAR.name().equals(type2)) {
                    goodsVo.setYearId(id);
                }
                if (DictGoodsType.SEX.name().equals(type2)) {
                    goodsVo.setSexId(id);
                }
                if (goodsService.findCount(goodsVo) > 0L) {
                    return true;
                }
            }
        }
        return false;
    }


}
