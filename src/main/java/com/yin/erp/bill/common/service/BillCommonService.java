package com.yin.erp.bill.common.service;

import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.entity.vo.out.BaseUploadMessage;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.bill.common.dao.BaseBillDao;
import com.yin.erp.bill.common.dao.BaseBillDetailDao;
import com.yin.erp.bill.common.dao.BaseBillGoodsDao;
import com.yin.erp.bill.common.entity.po.*;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.*;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.order.entity.po.OrderDetailPo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 单据服务
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class BillCommonService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;
    @Autowired
    private BillCommonExportService billCommonExportService;
    @Autowired
    private BillCommonImportService billCommonImportService;
    @Autowired
    private BillCommonSaveService billCommonSaveService;

    /**
     * 查询列表复用方法
     *
     * @param vo
     * @param dao
     * @param searchKey
     * @return
     * @throws MessageException
     */
    public BackPageVo<BillVo> findBillPage(SearchBillVo vo, JpaSpecificationExecutor dao, String[] searchKey) throws MessageException {
        Page page = dao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSupplierCode())) {
                predicates.add(criteriaBuilder.like(root.get("supplierCode"), "%" + vo.getSupplierCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getChannelCode())) {
                predicates.add(criteriaBuilder.like(root.get("channelCode"), "%" + vo.getChannelCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getToChannelCode())) {
                predicates.add(criteriaBuilder.like(root.get("toChannelCode"), "%" + vo.getToChannelCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getWarehouseCode())) {
                predicates.add(criteriaBuilder.like(root.get("warehouseCode"), "%" + vo.getWarehouseCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getParentBillCode())) {
                predicates.add(criteriaBuilder.like(root.get("parentBillCode"), "%" + vo.getParentBillCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getGrandParentBillCode())) {
                predicates.add(criteriaBuilder.like(root.get("grandParentBillCode"), "%" + vo.getGrandParentBillCode() + "%"));
            }
            if (vo.getStatusList() != null && !vo.getStatusList().isEmpty()) {
                Path<String> path = root.get("status");
                CriteriaBuilder.In<String> in = criteriaBuilder.in(path);
                for (String status : vo.getStatusList()) {
                    in.value(status);
                }
                predicates.add(in);
            }
            if (StringUtils.isNoneBlank(vo.getStartBillDate())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("billDate"), LocalDate.parse(vo.getStartBillDate())));
            }
            if (StringUtils.isNoneBlank(vo.getEndBillDate())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("billDate"), LocalDate.parse(vo.getEndBillDate())));
            }
            if (StringUtils.isNoneBlank(vo.getCreateUserName())) {
                predicates.add(criteriaBuilder.like(root.get("createUserName"), "%" + vo.getCreateUserName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getAuditUserName())) {
                predicates.add(criteriaBuilder.like(root.get("auditUserName"), "%" + vo.getAuditUserName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSearchKey())) {
                List<Predicate> predicatesSearch = new ArrayList<>();
                predicatesSearch.add(criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%"));
                predicatesSearch.add(criteriaBuilder.like(root.get("createUserName"), "%" + vo.getSearchKey() + "%"));
                predicatesSearch.add(criteriaBuilder.like(root.get("auditUserName"), "%" + vo.getSearchKey() + "%"));
                for (String key : searchKey) {
                    predicatesSearch.add(criteriaBuilder.like(root.get(key), "%" + vo.getSearchKey() + "%"));
                }
                predicates.add(criteriaBuilder.or(predicatesSearch.toArray(new Predicate[predicatesSearch.size()])));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        BackPageVo<BillVo> back = new BackPageVo();
        back.setTotalElements(page.getTotalElements());
        List<BillVo> list = new ArrayList<>();
        for (Object o : page.getContent()) {
            BillVo billVo = new BillVo();
            CopyUtil.copyProperties(billVo, o);
            list.add(billVo);
        }
        back.setContent(list);
        return back;
    }

    /**
     * 保存对象复用方法
     *
     * @param dbPo
     * @param vo
     * @param userSessionBo
     * @param billDao
     * @param billGoodsDao
     * @param billDetailDao
     * @param billPrefixKey
     * @throws MessageException
     */
    public BillPo save(BillPo dbPo, BillVo vo, UserSessionBo userSessionBo, BaseBillDao billDao, BaseBillGoodsDao billGoodsDao, BaseBillDetailDao billDetailDao, String billPrefixKey) throws MessageException {
        return this.save(dbPo, vo, userSessionBo, billDao, billGoodsDao, billDetailDao, billPrefixKey, null, null);
    }


    /**
     * 保存对象复用方法
     *
     * @param dbPo
     * @param vo
     * @param userSessionBo
     * @param billDao
     * @param billGoodsDao
     * @param billDetailDao
     * @param billPrefixKey
     * @param parentBillGoods
     * @throws MessageException
     */
    public BillPo save(BillPo dbPo, BillVo vo, UserSessionBo userSessionBo, BaseBillDao billDao, BaseBillGoodsDao billGoodsDao, BaseBillDetailDao billDetailDao, String billPrefixKey, List<BillGoodsVo> parentBillGoods, BaseBillDao parentBillDao) throws MessageException {
        return billCommonSaveService.save(dbPo, vo, userSessionBo, billDao, billGoodsDao, billDetailDao, billPrefixKey, parentBillGoods, parentBillDao);
    }

    /**
     * 删除
     *
     * @param id
     * @param billDao
     * @param billGoodsDao
     * @param billDetailDao
     * @throws MessageException
     */
    public void deleteById(String id, BaseBillDao billDao, BaseBillGoodsDao billGoodsDao, BaseBillDetailDao billDetailDao) throws MessageException {
        BillPo oldPo = (BillPo) billDao.findById(id).get();
        if (oldPo.getStatus().equals(BillStatusEnum.AUDITED.name()) || oldPo.getStatus().equals(BillStatusEnum.COMPLETE.name()) || oldPo.getStatus().equals(BillStatusEnum.QUOTE.name()) || oldPo.getStatus().equals(BillStatusEnum.PENDING.name())) {
            throw new MessageException("单据状态错误，请刷新后重试");
        }
        billDetailDao.deleteAllByBillId(id);
        billGoodsDao.deleteAllByBillId(id);
        billDao.deleteById(id);
    }

    /**
     * 审核
     *
     * @param id
     * @param vo
     * @param userSessionBo
     * @param d
     * @param billDao
     * @param billGoodsDao
     * @param billDetailDao
     * @throws MessageException
     */
    public void audit(String id, BaseAuditVo vo, UserSessionBo userSessionBo, Date d, BaseBillDao billDao, BaseBillGoodsDao billGoodsDao, BaseBillDetailDao billDetailDao) throws MessageException {
        BillPo po = (BillPo) billDao.findById(id).get();
        if (!po.getStatus().equals(BillStatusEnum.PENDING.name())) {
            throw new MessageException("单据状态错误，请刷新后重试");
        }
        po.setAuditUserId(userSessionBo.getId());
        po.setAuditUserName(userSessionBo.getName());
        po.setStatus(vo.getStatus());
        po.setAuditDate(d);
        billDao.save(po);
    }

    /**
     * 反审核
     *
     * @param id
     * @param billDao
     * @param billGoodsDao
     * @param billDetailDao
     * @throws MessageException
     */
    public void unAudit(String id, BaseBillDao billDao, BaseBillGoodsDao billGoodsDao, BaseBillDetailDao billDetailDao) throws MessageException {
        BillPo po = (BillPo) billDao.findById(id).get();
        if (po instanceof BillQuotedPo) {
            BillQuotedPo qpo = (BillQuotedPo) po;
            if (qpo.getTotalQuotedCount() > 0) {
                throw new MessageException("单据状态错误，请刷新后重试");
            }
        }
        if (!po.getStatus().equals(BillStatusEnum.AUDITED.name())) {
            throw new MessageException("单据状态错误，请刷新后重试");
        }
        po.setStatus("AUDIT_FAILURE");
        billDao.save(po);
    }


    /**
     * 查询单据明细复用
     *
     * @param id
     * @param billDao
     * @param baseBillGoodsDao
     * @param baseBillDetailDao
     * @return
     * @throws MessageException
     */
    public BillVo findById(String id, BaseBillDao billDao, BaseBillGoodsDao baseBillGoodsDao, BaseBillDetailDao baseBillDetailDao) throws MessageException {
        BillPo po = (BillPo) billDao.findById(id).get();
        BillVo vo = new BillVo();
        CopyUtil.copyProperties(vo, po);
        vo.setParentBillId(vo.getParentBillId() == null ? "" : vo.getParentBillId());
        vo.setGrandParentBillId(vo.getGrandParentBillId() == null ? "" : vo.getGrandParentBillId());
        List<BillGoodsVo> list = new ArrayList<>();
        List<BillGoodsPo> goodsList = baseBillGoodsDao.findByBillId(id);
        List<BillDetailPo> detailList = baseBillDetailDao.findByBillId(id);
        for (BillGoodsPo goodsPo : goodsList) {
            BillGoodsVo goodsVo = new BillGoodsVo();
            CopyUtil.copyProperties(goodsVo, goodsPo);
            List<BillDetailVo> detail = new ArrayList<>();
            for (BillDetailPo detailPo : detailList.stream().filter(d -> d.getBillGoodsId().equals(goodsPo.getId())).collect(Collectors.toList())) {
                BillDetailVo billDetailVo = new BillDetailVo();
                CopyUtil.copyProperties(billDetailVo, detailPo);
                billDetailVo.setColorId(detailPo.getGoodsColorId());
                billDetailVo.setSizeId(detailPo.getGoodsSizeId());
                billDetailVo.setColorName(detailPo.getGoodsColorName());
                billDetailVo.setColorCode(detailPo.getGoodsColorCode());
                billDetailVo.setSizeName(detailPo.getGoodsSizeName());
                billDetailVo.setBillCount(detailPo.getBillCount());
                detail.add(billDetailVo);
            }
            goodsVo.setDetail(detail);
            list.add(goodsVo);
        }
        vo.setGoodsList(list);
        return vo;
    }


    /**
     * 上传单据
     *
     * @param file
     * @param userSessionBo
     * @param billService
     * @param redisKey
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo, BillService billService, String redisKey) {
        this.uploadBill(file, userSessionBo, billService, redisKey, null);
    }

    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo, BillService billService, String redisKey, BaseBillDao parentBaseBillDao) {
        this.uploadBill(file, userSessionBo, billService, redisKey, parentBaseBillDao, null);
    }

    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo, BillService billService, String redisKey, BaseBillDao parentBaseBillDao, BaseBillDao grandParentBaseBillDao) {
        try {
            ValueOperations operations = redisTemplate.opsForValue();
            LocalDateTime startTime = LocalDateTime.now();
            try {
                Workbook workbook = WorkbookFactory.create(file.getInputStream());
                billCommonImportService.uploadBill(workbook, userSessionBo, billService, redisKey, parentBaseBillDao, grandParentBaseBillDao, startTime);
            } catch (MessageException e) {
                operations.set(userSessionBo.getId() + ":upload:bill:" + redisKey, new BaseUploadMessage(-1, TimeUtil.useTime(startTime), e.getMessage()), 10L, TimeUnit.MINUTES);
            } catch (Throwable e) {
                operations.set(userSessionBo.getId() + ":upload:bill:" + redisKey, new BaseUploadMessage(-1, TimeUtil.useTime(startTime), e.getMessage()), 10L, TimeUnit.MINUTES);
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * 单据导出
     *
     * @param vo
     * @param response
     * @param billService
     * @param baseBillGoodsDao
     * @param baseBillDetailDao
     * @param sourceKey
     * @param targetKey
     * @param hasParent
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response, BillService billService, BaseBillGoodsDao baseBillGoodsDao, BaseBillDetailDao baseBillDetailDao,
                       String sourceKey, String targetKey, boolean hasParent) throws Exception {
        billCommonExportService.export(vo, response, billService, baseBillGoodsDao, baseBillDetailDao, sourceKey, targetKey, hasParent);
    }

    /**
     * 查询上游单据
     *
     * @param code
     * @param baseBillDao
     * @return
     */
    public Page<BillPo> findParentBill(String code, BaseBillDao baseBillDao, String[] searchKey) {
        Page<BillPo> page = baseBillDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), BillStatusEnum.AUDITED.name()));
            if (StringUtils.isNoneBlank(code)) {
                List<Predicate> predicatesSearch = new ArrayList<>();
                predicatesSearch.add(criteriaBuilder.like(root.get("code"), "%" + code + "%"));
                for (String key : searchKey) {
                    predicatesSearch.add(criteriaBuilder.equal(root.get(key), code));
                }
                predicates.add(criteriaBuilder.or(predicatesSearch.toArray(new Predicate[predicatesSearch.size()])));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(0, 10, Sort.Direction.DESC, "createDate"));
        return page;
    }


    /**
     * 查询上级单据明细
     *
     * @param id
     * @param baseBillGoodsDao
     * @param baseBillDetailDao
     * @return
     * @throws MessageException
     */
    public List<BillGoodsVo> findParentGoodsList(String id, BaseBillGoodsDao baseBillGoodsDao, BaseBillDetailDao baseBillDetailDao) throws MessageException {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        List<BillGoodsVo> list = new ArrayList<>();
        List<BillGoodsPo> goodsList = baseBillGoodsDao.findByBillId(id);
        List<BillDetailPo> detailList = baseBillDetailDao.findByBillId(id);
        for (BillGoodsPo goodsPo : goodsList) {
            BillGoodsVo goodsVo = new BillGoodsVo();
            CopyUtil.copyProperties(goodsVo, goodsPo);
            List<BillDetailVo> detail = new ArrayList<>();
            for (BillDetailPo detailPo : detailList.stream().filter(d -> d.getBillGoodsId().equals(goodsPo.getId())).collect(Collectors.toList())) {
                BillDetailVo billDetailVo = new BillDetailVo();
                CopyUtil.copyProperties(billDetailVo, detailPo);
                billDetailVo.setColorId(detailPo.getGoodsColorId());
                billDetailVo.setSizeId(detailPo.getGoodsSizeId());
                billDetailVo.setColorCode(detailPo.getGoodsColorCode());
                billDetailVo.setColorName(detailPo.getGoodsColorName());
                billDetailVo.setSizeName(detailPo.getGoodsSizeName());
                billDetailVo.setBillCount(detailPo.getBillCount());
                detail.add(billDetailVo);
            }
            goodsVo.setDetail(detail);
            list.add(goodsVo);
        }
        return list;
    }

    /**
     * 修改一对一的上游单据状态
     *
     * @param id
     * @param status
     * @param baseBillDao
     * @param parentBaseBillDao
     */
    public void changeOneToOneParentStatus(String id, BillStatusEnum status, BaseBillDao baseBillDao, BaseBillDao parentBaseBillDao) throws MessageException {
        //修改上游单据状态
        if (StringUtils.isNotBlank(id)) {
            BillPo billPo = (BillPo) baseBillDao.findById(id).get();
            if (StringUtils.isNotBlank(billPo.getParentBillId())) {
                BillPo parent = (BillPo) parentBaseBillDao.findById(billPo.getParentBillId()).get();
                parent.setStatus(status.name());
                parentBaseBillDao.save(parent);
            }
        }
    }

    /**
     * 修改一对一的上游单据状态
     *
     * @param id
     * @param status
     * @param parentBaseBillDao
     * @param baseBillDao
     * @param childBillId       上游单据的子单据
     */
    public void changeOneToOneParentStatus(String id, BillStatusEnum status, BaseBillDao baseBillDao, BaseBillDao parentBaseBillDao, String childBillId) throws MessageException {
        //修改上游单据状态
        if (StringUtils.isNotBlank(id)) {
            BillPo billPo = (BillPo) baseBillDao.findById(id).get();
            if (StringUtils.isNotBlank(billPo.getParentBillId())) {
                BillPo parent = (BillPo) parentBaseBillDao.findById(billPo.getParentBillId()).get();
                parent.setStatus(status.name());
                parent.setChildBillId(childBillId);
                parentBaseBillDao.save(parent);
            }
        }
    }

    /**
     * 释放引用数量
     *
     * @param po
     * @param baseBillDetailDao
     * @param parentBaseBillDao
     * @param parentBaseBillDetailDao
     */
    public void freeManyToOneCount(BillPo po, BaseBillDetailDao baseBillDetailDao, BaseBillDao parentBaseBillDao, BaseBillDetailDao parentBaseBillDetailDao) {
        Integer totalCount = 0;
        List<BillDetailPo> detailList = baseBillDetailDao.findByBillId(po.getId());
        List<BillDetailPo> parentDetailList = parentBaseBillDetailDao.findByBillId(po.getParentBillId());
        for (BillDetailPo parentDetail : parentDetailList) {
            Optional<BillDetailPo> detailOptional = detailList.stream().filter(d -> d.getGoodsId().equals(parentDetail.getGoodsId()) && d.getGoodsColorId().equals(parentDetail.getGoodsColorId()) && d.getGoodsSizeId().equals(parentDetail.getGoodsSizeId())).findFirst();
            if (detailOptional.isPresent()) {
                BillDetailPo detailPo = detailOptional.get();
                BillDetailQuotedPo pd = (BillDetailQuotedPo) parentDetail;
                pd.setQuotedCount(pd.getQuotedCount() - detailPo.getBillCount());
                totalCount += detailPo.getBillCount();
                parentBaseBillDetailDao.save(pd);
            }
        }
        BillPo billQuotedPo = (BillPo) parentBaseBillDao.findById(po.getParentBillId()).get();
        billQuotedPo.setTotalQuotedCount(billQuotedPo.getTotalQuotedCount() - totalCount);
        billQuotedPo.setStatus(billQuotedPo.getTotalQuotedCount() < billQuotedPo.getTotalCount() ? BillStatusEnum.AUDITED.name() : BillStatusEnum.COMPLETE.name());
        parentBaseBillDao.save(billQuotedPo);
    }

    /**
     * 减少上游数量
     *
     * @param po
     * @param baseBillDetailDao
     * @param parentBaseBillDao
     * @param parentBaseBillDetailDao
     */
    public void cutManyToOneCount(BillPo po, BaseBillDetailDao baseBillDetailDao, BaseBillDao parentBaseBillDao, BaseBillDetailDao parentBaseBillDetailDao) {
        Integer totalCount = 0;
        List<BillDetailPo> detailList = baseBillDetailDao.findByBillId(po.getId());
        List<BillDetailPo> parentDetailList = parentBaseBillDetailDao.findByBillId(po.getParentBillId());
        for (BillDetailPo parentDetail : parentDetailList) {
            Optional<BillDetailPo> detailOptional = detailList.stream().filter(d -> d.getGoodsId().equals(parentDetail.getGoodsId()) && d.getGoodsColorId().equals(parentDetail.getGoodsColorId()) && d.getGoodsSizeId().equals(parentDetail.getGoodsSizeId())).findFirst();
            if (detailOptional.isPresent()) {
                BillDetailPo detailPo = detailOptional.get();
                BillDetailQuotedPo pd = (BillDetailQuotedPo) parentDetail;
                pd.setQuotedCount(pd.getQuotedCount() + detailPo.getBillCount());
                totalCount += detailPo.getBillCount();
//                    if (pd.getQuotedCount() > pd.getBillCount()) {
//                        throw new MessageException("上游单据数量已经不足引用");
//                    }
                parentBaseBillDetailDao.save((OrderDetailPo) pd);
            }
        }
        BillPo billQuotedPo = (BillPo) parentBaseBillDao.findById(po.getParentBillId()).get();
        billQuotedPo.setTotalQuotedCount(billQuotedPo.getTotalQuotedCount() + totalCount);
        billQuotedPo.setStatus(billQuotedPo.getTotalQuotedCount() < billQuotedPo.getTotalCount() ? BillStatusEnum.AUDITED.name() : BillStatusEnum.COMPLETE.name());
//            if (billQuotedPo.getTotalQuotedCount() > billQuotedPo.getTotalCount()) {
//                throw new MessageException("上游单据数量已经不足引用");
//            }
        parentBaseBillDao.save(billQuotedPo);
    }


    /**
     * 上游审核自动生成下游单据
     *
     * @param po
     * @param baseBillGoodsDao
     * @param baseBillDetailDao
     * @param subBillService
     * @param userSessionBo
     * @throws MessageException
     */
    public void billCreateSubBill(BillPo po, BaseBillGoodsDao baseBillGoodsDao, BaseBillDetailDao baseBillDetailDao, BillService subBillService, UserSessionBo userSessionBo) throws MessageException {
        BillVo billVo = new BillVo();
        billVo.setStatus(BillStatusEnum.PENDING.name());
        billVo.setBillDate(po.getBillDate());
        billVo.setParentBillCode(po.getCode());
        billVo.setParentBillId(po.getId());
        billVo.setChannelName(po.getChannelName());
        billVo.setChannelCode(po.getChannelCode());
        billVo.setChannelId(po.getChannelId());
        billVo.setToChannelName(po.getToChannelName());
        billVo.setToChannelCode(po.getToChannelCode());
        billVo.setToChannelId(po.getToChannelId());
        billVo.setWarehouseName(po.getWarehouseName());
        billVo.setWarehouseId(po.getWarehouseId());
        billVo.setWarehouseCode(po.getWarehouseCode());
        List<BillGoodsVo> billGoodsVoList = new ArrayList<>();
        List<BillGoodsPo> goodsListPo = baseBillGoodsDao.findByBillId(po.getId());
        List<BillDetailPo> detailListPo = baseBillDetailDao.findByBillId(po.getId());
        for (BillGoodsPo billGoodsPo : goodsListPo) {
            BillGoodsVo billGoodsVo = new BillGoodsVo();
            billGoodsVo.setGoodsCode(billGoodsPo.getGoodsCode());
            billGoodsVo.setTagPrice(billGoodsPo.getTagPrice());
            billGoodsVo.setPrice(billGoodsPo.getPrice());
            billGoodsVo.setGoodsName(billGoodsPo.getGoodsName());
            billGoodsVo.setGoodsId(billGoodsPo.getGoodsId());
            List<BillDetailVo> billDetailVoList = new ArrayList<>();
            detailListPo.stream().filter(dp -> dp.getGoodsId().equals(billGoodsPo.getGoodsId())).forEach(dp -> {
                BillDetailVo billDetailVo = new BillDetailVo();
                billDetailVo.setBillCount(dp.getBillCount());
                billDetailVo.setSizeId(dp.getGoodsSizeId());
                billDetailVo.setColorId(dp.getGoodsColorId());
                billDetailVoList.add(billDetailVo);
            });
            billGoodsVo.setDetail(billDetailVoList);
            billGoodsVoList.add(billGoodsVo);
        }
        billVo.setGoodsList(billGoodsVoList);
        BillPo billPo = subBillService.save(billVo, userSessionBo);
        BaseAuditVo baseAuditVo = new BaseAuditVo();
        baseAuditVo.setIds(Arrays.asList(billPo.getId()));
        baseAuditVo.setStatus(BillStatusEnum.AUDITED.name());
        subBillService.audit(baseAuditVo, userSessionBo);
    }

}
