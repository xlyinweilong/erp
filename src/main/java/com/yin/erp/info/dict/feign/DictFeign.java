package com.yin.erp.info.dict.feign;

import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.bo.DictSizeBo;
import com.yin.erp.info.dict.entity.po.DictPo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 字典对外服务
 *
 * @author yin.weilong
 * @date 2018.11.13
 */
@Service
public class DictFeign {

    @Autowired
    private DictDao dictDao;
    @Autowired
    private DictSizeDao dictSizeDao;

    /**
     * 通过ID获取名称
     *
     * @param id
     * @return
     */
    public String getNameById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        Optional<DictPo> optional = dictDao.findById(id);
        return optional.isPresent() ? optional.get().getName() : null;
    }

    /**
     * 通过ID获取编号
     *
     * @param id
     * @return
     */
    public String getCodeById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        Optional<DictPo> optional = dictDao.findById(id);
        return optional.isPresent() ? optional.get().getCode() : null;
    }

    /**
     * 根据尺码组ID查询尺码列表
     *
     * @param sizeGroupId
     * @return
     */
    public List<DictSizeBo> findDictSizePo(String sizeGroupId) {
        List<DictSizeBo> list = new ArrayList<>();
        dictSizeDao.findByGroupId(sizeGroupId).forEach(sizePo -> list.add(DictSizeBo.builder().id(sizePo.getId()).name(sizePo.getName()).sizeGroupId(sizePo.getGroupId()).build()));
        return list;
    }

    /**
     * 根据尺码ID获取尺码名称
     *
     * @param sizeId
     * @return
     */
    public String findSizeNamenById(String sizeId) {
        return dictSizeDao.findById(sizeId).get().getName();
    }

}
