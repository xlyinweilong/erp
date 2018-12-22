package com.yin.erp.info.channel.service;

import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.channel.entity.vo.ChannelVo;
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
 * 渠道服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ChannelService {

    @Autowired
    private ChannelDao channelDao;

    /**
     * 保存
     *
     * @param vo
     * @throws Exception
     */
    public void save(ChannelVo vo) throws MessageException {
        ChannelPo po = new ChannelPo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = channelDao.findById(vo.getId()).get();
            //发送给队列，全局做数据更新 TODO
        }
        po.setCode(vo.getCode());
        po.setName(vo.getName());
        channelDao.save(po);
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public ChannelVo findById(String id) {
        ChannelPo po = channelDao.findById(id).get();
        ChannelVo vo = new ChannelVo();
        vo.setId(po.getId());
        vo.setCode(po.getCode());
        vo.setName(po.getName());
        return vo;
    }

    /**
     * 查询字典
     *
     * @param vo
     * @return
     */
    public Page<ChannelPo> findDictPage(ChannelVo vo) {
        Page<ChannelPo> page = channelDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getName() + "%"));
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
            channelDao.deleteById(id);
        }
    }


}
