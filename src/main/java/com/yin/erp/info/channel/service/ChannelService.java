package com.yin.erp.info.channel.service;

import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.config.sysconfig.dao.ConfigChannelDao;
import com.yin.erp.config.sysconfig.entity.po.ConfigChannelPo;
import com.yin.erp.config.sysconfig.entity.po.ConfigPo;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.channel.entity.vo.ChannelVo;
import com.yin.erp.info.dict.feign.DictFeign;
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
    @Autowired
    private DictFeign dictFeign;
    @Autowired
    private ConfigChannelDao configChannelDao;


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
        po.setGroupId(vo.getGroupId());
        po.setGroupName(dictFeign.getNameById(vo.getGroupId()));
        channelDao.save(po);
        configChannelDao.deleteAllByChannelId(po.getId());
        for (ConfigPo configPo : vo.getChannelConfigList()) {
            configChannelDao.save(new ConfigChannelPo(configPo.getId(), po.getId(), configPo.getDefaultValue()));
        }
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
        vo.setGroupId(po.getGroupId());
        vo.setGroupName(po.getGroupName());
        vo.setChannelConfigChannelList(configChannelDao.findByChannelId(id));
        return vo;
    }

    /**
     * 查询字典
     *
     * @param vo
     * @return
     */
    public Page<ChannelPo> findChannelPage(ChannelVo vo, UserSessionBo user) {
        Page<ChannelPo> page = channelDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getName() + "%"));
            }
            Predicate p1 = criteriaBuilder.isNull(root.get("groupId"));
            if (!user.getChannelGroupIds().isEmpty()) {
                predicates.add(criteriaBuilder.or(p1, criteriaBuilder.in(root.get("groupId")).value(user.getChannelGroupIds())));
            } else {
                predicates.add(p1);
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
            configChannelDao.deleteAllByChannelId(id);
        }
    }


}
