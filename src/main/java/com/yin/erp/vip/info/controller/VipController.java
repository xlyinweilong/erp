package com.yin.erp.vip.info.controller;

import com.yin.common.controller.BaseJson;
import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BaseUploadMessage;
import com.yin.common.exceptions.MessageException;
import com.yin.common.service.LoginService;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.employ.dao.EmployDao;
import com.yin.erp.info.employ.entity.po.EmployPo;
import com.yin.erp.vip.info.dao.VipDao;
import com.yin.erp.vip.info.entity.po.VipPo;
import com.yin.erp.vip.info.entity.vo.VipVo;
import com.yin.erp.vip.info.service.VipService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private LoginService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private VipService vipService;


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
            VipPo vipPo = vipDao.findById(id).get();
            if (vipPo.getBalance().compareTo(BigDecimal.ZERO) > 0) {
                throw new MessageException("账户：" + vipPo.getCode() + "还有余额，不能删除");
            }
            vipDao.deleteById(id);
        }
        return BaseJson.getSuccess();
    }

    /**
     * 上传会员
     *
     * @param file
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/upload")
    public BaseJson updateBarCode(@RequestParam("file") MultipartFile file, javax.servlet.http.HttpServletRequest request) throws Exception {
        UserSessionBo userSessionBo = userService.getUserSession(request);
        ValueOperations operations = redisTemplate.opsForValue();
        LocalDateTime startTime = LocalDateTime.now();
        operations.set(userSessionBo.getId() + ":upload:vip_info", new BaseUploadMessage(), 10L, TimeUnit.MINUTES);
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            vipService.update(workbook, userSessionBo,startTime);
        } catch (Throwable e) {
            operations.set(userSessionBo.getId() + ":upload:vip_info", new BaseUploadMessage(-1, TimeUtil.useTime(startTime), e.getMessage()), 10L, TimeUnit.MINUTES);
            e.printStackTrace();
        }
        return BaseJson.getSuccess("文件上传成功");
    }


}
