package com.yin.erp.vip.info.controller;

import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.employ.dao.EmployDao;
import com.yin.erp.info.employ.entity.po.EmployPo;
import com.yin.erp.vip.info.dao.VipDao;
import com.yin.erp.vip.info.entity.po.VipPo;
import com.yin.erp.vip.info.entity.vo.VipVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员控制器
 *
 * @author yin
 */
@RestController
@RequestMapping(value = "api/vip/vip")
@Transactional(rollbackFor = Throwable.class)
public class VipController {

    @Autowired
    private VipDao vipDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private EmployDao employDao;


    /**
     * 保存
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "save", consumes = "application/json")
    public BaseJson save(@Validated @RequestBody VipVo vo) throws MessageException {
        VipPo po = new VipPo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = vipDao.findById(vo.getId()).get();
        }
        po.setName(vo.getName());
        po.setOpenChannelId(vo.getOpenChannelId());
        po.setOpenEmployId(vo.getOpenEmployId());
        po.setOpenDate(vo.getOpenDate());
        po.setSex(vo.getSex());
        po.setCode(vo.getCode());
        po.setOpenEmployName(null);
        po.setOpenEmployCode(null);
        po.setOpenChannelCode(null);
        po.setOpenChannelName(null);
        if (StringUtils.isNotBlank(vo.getOpenChannelId())) {
            ChannelPo channelPo = channelDao.findById(vo.getOpenChannelId()).get();
            po.setOpenChannelCode(channelPo.getCode());
            po.setOpenChannelName(channelPo.getName());
        }
        if (StringUtils.isNotBlank(vo.getOpenEmployId())) {
            EmployPo employPo = employDao.findById(vo.getOpenEmployId()).get();
            po.setOpenEmployName(employPo.getName());
            po.setOpenEmployCode(employPo.getCode());
        }
        vipDao.save(po);
        return BaseJson.getSuccess();
    }

    /**
     * 获取列表
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @GetMapping(value = "list")
    public BaseJson list(VipVo vo) throws MessageException {
        Page<VipPo> page = vipDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(vo.getName())) {
                predicates.add(criteriaBuilder.equal(root.get("name"), vo.getName()));
            }
            if (StringUtils.isNotBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.equal(root.get("code"), vo.getCode()));
            }
            if (StringUtils.isNoneBlank(vo.getSearchKey())) {
                List<Predicate> predicatesSearch = new ArrayList<>();
                predicatesSearch.add(criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%"));
                predicatesSearch.add(criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%"));
                predicatesSearch.add(criteriaBuilder.equal(root.get("openChannelCode"), vo.getSearchKey()));
                predicatesSearch.add(criteriaBuilder.equal(root.get("openChannelName"), vo.getSearchKey()));
                predicatesSearch.add(criteriaBuilder.equal(root.get("openEmployCode"), vo.getSearchKey()));
                predicatesSearch.add(criteriaBuilder.equal(root.get("openEmployName"), vo.getSearchKey()));
                if ("女".equals(vo.getSearchKey())) {
                    predicatesSearch.add(criteriaBuilder.equal(root.get("sex"), 0));
                }
                if ("男".equals(vo.getSearchKey())) {
                    predicatesSearch.add(criteriaBuilder.equal(root.get("sex"), 1));
                }
                predicates.add(criteriaBuilder.or(predicatesSearch.toArray(new Predicate[predicatesSearch.size()])));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        return BaseJson.getSuccess(page);
    }

    /**
     * 删除
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @PostMapping(value = "delete")
    public BaseJson delete(@RequestBody BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            vipDao.deleteById(id);
        }
        return BaseJson.getSuccess();
    }


}
