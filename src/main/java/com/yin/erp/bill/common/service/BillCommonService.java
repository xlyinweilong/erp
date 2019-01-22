package com.yin.erp.bill.common.service;

import com.yin.erp.base.entity.po.BasePo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.entity.vo.out.BaseUploadMessage;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.bill.common.dao.BaseBillDao;
import com.yin.erp.bill.common.dao.BaseBillDetailDao;
import com.yin.erp.bill.common.dao.BaseBillGoodsDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.BillDetailVo;
import com.yin.erp.bill.common.entity.vo.in.BillGoodsVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.po.DictPo;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.supplier.dao.SupplierDao;
import com.yin.erp.info.supplier.entity.po.SupplierPo;
import com.yin.erp.info.warehouse.dao.WarehouseDao;
import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private ChannelDao channelDao;
    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private DictDao dictDao;
    @Autowired
    private DictSizeDao dictSizeDao;
    @Autowired
    private GoodsColorDao goodsColorDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;

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
            if (StringUtils.isNoneBlank(vo.getWarehouseCode())) {
                predicates.add(criteriaBuilder.like(root.get("warehouseCode"), "%" + vo.getWarehouseCode() + "%"));
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
    public void save(BillPo dbPo, BillVo vo, UserSessionBo userSessionBo, BaseBillDao billDao, BaseBillGoodsDao billGoodsDao, BaseBillDetailDao billDetailDao, String billPrefixKey) throws MessageException {
        this.save(dbPo, vo, userSessionBo, billDao, billGoodsDao, billDetailDao, billPrefixKey, null);
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
    public void save(BillPo dbPo, BillVo vo, UserSessionBo userSessionBo, BaseBillDao billDao, BaseBillGoodsDao billGoodsDao, BaseBillDetailDao billDetailDao, String billPrefixKey, List<BillGoodsVo> parentBillGoods) throws MessageException {
        CopyUtil.copyProperties(dbPo, vo);
        if (StringUtils.isBlank(dbPo.getId())) {
            dbPo.setId(GenerateUtil.createUUID());
            dbPo.setCode(billPrefixKey + GenerateUtil.createSerialNumber());
            dbPo.setCreateUserId(userSessionBo.getId());
            dbPo.setCreateUserName(userSessionBo.getName());
        } else {
            dbPo.setVersion(((BasePo) billDao.findById(dbPo.getId()).get()).getVersion());
            //删除
            billDetailDao.deleteAllByBillId(dbPo.getId());
            billGoodsDao.deleteAllByBillId(dbPo.getId());
        }
        dbPo.setTotalCount(0);
        dbPo.setTotalAmount(BigDecimal.ZERO);
        dbPo.setTotalTagAmount(BigDecimal.ZERO);
        if (StringUtils.isNotBlank(vo.getChannelId())) {
            ChannelPo channelPo = channelDao.findById(vo.getChannelId()).get();
            dbPo.setChannelCode(channelPo.getCode());
            dbPo.setChannelName(channelPo.getName());
        }
        if (StringUtils.isNotBlank(vo.getToChannelId())) {
            ChannelPo channelPo = channelDao.findById(vo.getToChannelId()).get();
            dbPo.setToChannelCode(channelPo.getCode());
            dbPo.setToChannelName(channelPo.getName());
        }
        if (StringUtils.isNotBlank(vo.getSupplierId())) {
            SupplierPo supplierPo = supplierDao.findById(vo.getSupplierId()).get();
            dbPo.setSupplierName(supplierPo.getName());
            dbPo.setSupplierCode(supplierPo.getCode());
        }
        if (StringUtils.isNotBlank(vo.getWarehouseId())) {
            WarehousePo warehousePo = warehouseDao.findById(vo.getWarehouseId()).get();
            dbPo.setWarehouseName(warehousePo.getName());
            dbPo.setWarehouseCode(warehousePo.getCode());
        }
        List<BillGoodsPo> goodsList = new ArrayList<>();
        List<BillDetailPo> detailList = new ArrayList<>();
        for (BillGoodsVo goodsVo : vo.getGoodsList()) {
            GoodsPo goodsPo = goodsDao.findById(goodsVo.getGoodsId()).get();
            if (parentBillGoods != null) {
                Optional<BillGoodsVo> dbGoods = parentBillGoods.stream().filter(g -> g.getGoodsId().equals(goodsVo.getGoodsId())).findFirst();
                if (!dbGoods.isPresent()) {
                    throw new MessageException("上游单据没有货号：" + goodsPo.getCode());
                }
            }
            BillGoodsPo billGoodsPo = dbPo.getBillGoodsInstance();
            billGoodsPo.setBillDate(vo.getBillDate());
            billGoodsPo.setId(GenerateUtil.createUUID());
            billGoodsPo.setGoodsCode(goodsPo.getCode());
            billGoodsPo.setGoodsId(goodsPo.getId());
            billGoodsPo.setGoodsName(goodsPo.getName());
            billGoodsPo.setPrice(goodsVo.getPrice());
            billGoodsPo.setBillId(dbPo.getId());
            billGoodsPo.setTagPrice(goodsVo.getTagPrice());
            for (BillDetailVo billDetailVo : goodsVo.getDetail()) {
                DictPo colorPo = dictDao.findById(billDetailVo.getColorId()).get();
                DictSizePo sizePo = dictSizeDao.findById(billDetailVo.getSizeId()).get();
                if (parentBillGoods != null) {
                    Optional<BillGoodsVo> dbGoods = parentBillGoods.stream().filter(g -> g.getGoodsId().equals(goodsVo.getGoodsId())).findFirst();
                    Optional<BillDetailVo> dbDetail = dbGoods.get().getDetail().stream().filter(d -> d.getSizeId().equals(billDetailVo.getSizeId()) && d.getColorId().equals(billDetailVo.getColorId())).findFirst();
                    if (!dbDetail.isPresent()) {
                        throw new MessageException("上游单据没有货号：" + goodsVo.getGoodsCode() + ",颜色编号：" + colorPo.getCode() + ",颜色名称：" + colorPo.getName() + ",尺码：" + sizePo.getName());
                    }
                    if (dbDetail.get().getBillCount() < billDetailVo.getBillCount()) {
                        throw new MessageException("上游单据里货号：" + goodsVo.getGoodsCode() + ",颜色编号：" + colorPo.getCode() + ",颜色名称：" + colorPo.getName() + ",尺码：" + sizePo.getName()
                                + "的数量不足，只有：" + dbDetail.get().getBillCount());
                    }
                }
                BillDetailPo billDetailPo = dbPo.getBillDetailInstance();
                billDetailPo.setBillCount(billDetailVo.getBillCount());
                billDetailPo.setBillDate(dbPo.getBillDate());
                billDetailPo.setBillGoodsId(billGoodsPo.getId());
                billDetailPo.setBillId(dbPo.getId());
                billDetailPo.setGoodsCode(goodsPo.getCode());
                billDetailPo.setGoodsId(goodsPo.getId());
                billDetailPo.setGoodsName(goodsPo.getName());
                billDetailPo.setGoodsColorCode(colorPo.getCode());
                billDetailPo.setGoodsColorId(colorPo.getId());
                billDetailPo.setGoodsColorName(colorPo.getName());
                billDetailPo.setGoodsSizeId(sizePo.getId());
                billDetailPo.setGoodsSizeName(sizePo.getName());
                billDetailPo.setPrice(billGoodsPo.getPrice());
                billDetailPo.setTagPrice(billGoodsPo.getTagPrice());
                detailList.add(billDetailPo);
                //求和
                billGoodsPo.setBillCount(billGoodsPo.getBillCount() + billDetailVo.getBillCount());
            }
            goodsList.add(billGoodsPo);
            //求和
            dbPo.setTotalCount(dbPo.getTotalCount() + billGoodsPo.getBillCount());
            dbPo.setTotalAmount(dbPo.getTotalAmount().add(billGoodsPo.getPrice().multiply(BigDecimal.valueOf(billGoodsPo.getBillCount()))));
            dbPo.setTotalTagAmount(dbPo.getTotalTagAmount().add(billGoodsPo.getTagPrice().multiply(BigDecimal.valueOf(billGoodsPo.getBillCount()))));
        }
        billDetailDao.saveAll(detailList);
        billGoodsDao.saveAll(goodsList);
        billDao.saveAndFlush(dbPo);
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
                detail.add(billDetailVo);
            }
            goodsVo.setDetail(detail);
            list.add(goodsVo);
        }
        vo.setGoodsList(list);
        return vo;
    }


    /**
     * 上传单据复用
     *
     * @param file
     * @param userSessionBo
     * @param billService
     * @param redisKey
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo, BillService billService, String redisKey) {
        this.uploadBill(file, userSessionBo, billService, redisKey, null);
    }

    /**
     * 上传单据复用
     *
     * @param file
     * @param userSessionBo
     * @param billService
     * @param redisKey
     */
//    @Async
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo, BillService billService, String redisKey, BaseBillDao baseBillDao) {
        ValueOperations operations = redisTemplate.opsForValue();
        LocalDateTime startTime = LocalDateTime.now();
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int count = 0;
            //准备数据
            BillVo vo = new BillVo();
            vo.setStatus("PENDING");
            List<BillGoodsVo> billGoodsList = new ArrayList<>();
            vo.setGoodsList(billGoodsList);
            boolean success = true;
            int errorCellNum = 10;
            for (Row row : sheet) {
                count++;
                operations.set(userSessionBo.getId() + ":upload:bill:" + redisKey, new BaseUploadMessage(sheet.getLastRowNum() + 1, count), 10L, TimeUnit.MINUTES);
                if (count == 1) {
                    row.createCell(errorCellNum).setCellValue("错误信息");
                    continue;
                }
                try {
                    //获取数据
                    Date date = ExcelReadUtil.getDate(row.getCell(0));
                    String parentBillCode = billService.uploadBillParentBillCode(row);
                    String channelCode = billService.uploadBillChannelCode(row);
                    String supplierCode = billService.uploadBillSupplierCode(row);
                    String warehouseCode = billService.uploadBillWarehouseCode(row);
                    String goodsCode = billService.uploadBillGoodsCode(row);
                    String goodsColorCode = billService.uploadBillGoodsColorCode(row);
                    String goodsColorName = billService.uploadBillGoodsColorName(row);
                    String goodsSizeName = billService.uploadBillGoodsSizeName(row);
                    BigDecimal price = billService.uploadBillPrice(row);
                    Integer billCount = billService.uploadBillBillCount(row);

                    if (date == null || StringUtils.isBlank(goodsCode) || StringUtils.isBlank(goodsColorCode) || StringUtils.isBlank(goodsColorName)
                            || StringUtils.isBlank(goodsSizeName) || billCount == null
                            || StringUtils.isBlank(ExcelReadUtil.getString(row.getCell(1))) || StringUtils.isBlank(ExcelReadUtil.getString(row.getCell(2)))) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "缺少必要数据");
                        success = false;
                        continue;
                    }

                    if (vo.getBillDate() == null) {
                        vo.setBillDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    }

                    if (vo.getParentBillId() == null && parentBillCode != null) {
                        BillPo billPo = baseBillDao.findByCode(parentBillCode);
                        if (billPo == null) {
                            ExcelReadUtil.addErrorToRow(row, errorCellNum, "上游单据没找到");
                            success = false;
                        }
                        vo.setParentBillId(billPo.getId());
                        vo.setParentBillCode(billPo.getCode());
                        vo.setWarehouseId(billPo.getWarehouseId());
                        vo.setWarehouseCode(billPo.getWarehouseCode());
                        vo.setWarehouseName(billPo.getWarehouseName());
                        vo.setChannelId(billPo.getChannelId());
                        vo.setChannelCode(billPo.getChannelCode());
                        vo.setChannelName(billPo.getChannelName());
                    }

                    if (vo.getSupplierId() == null && supplierCode != null) {
                        SupplierPo supplierPo = supplierDao.findByCode(supplierCode);
                        if (supplierPo == null) {
                            ExcelReadUtil.addErrorToRow(row, errorCellNum, "供应商没找到");
                            success = false;
                        }
                        vo.setSupplierId(supplierPo.getId());
                    }
                    if (vo.getWarehouseId() == null && warehouseCode != null) {
                        WarehousePo warehousePo = warehouseDao.findByCode(warehouseCode);
                        if (warehousePo == null) {
                            ExcelReadUtil.addErrorToRow(row, errorCellNum, "渠道没找到");
                            success = false;
                        }
                        vo.setWarehouseId(warehousePo.getId());
                    }
                    if (vo.getChannelId() == null && channelCode != null) {
                        ChannelPo channelPo = channelDao.findByCode(channelCode);
                        if (channelPo == null) {
                            ExcelReadUtil.addErrorToRow(row, errorCellNum, "仓库没找到");
                            success = false;
                        }
                        vo.setChannelId(channelPo.getId());
                    }

                    //判断是否匹配货号、颜色、内长、尺码
                    GoodsPo goodsPo = goodsDao.findByCode(goodsCode);
                    if (goodsPo == null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "货号不存在");
                        continue;
                    }
                    List<GoodsColorPo> colorList = goodsColorDao.findByGoodsId(goodsPo.getId());
                    GoodsColorPo goodsColorPo = colorList.stream().filter(g -> g.getColorCode().equals(goodsColorCode) && g.getColorName().equals(goodsColorName)).findFirst().get();
                    if (goodsColorPo == null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "颜色编号和名称没定位颜色");
                        success = false;
                        continue;
                    }
                    //尺码
                    List<DictSizePo> sizeList = dictSizeDao.findByGroupId(goodsPo.getSizeGroupId());
                    Optional<DictSizePo> dictSizePo = sizeList.stream().filter(g -> g.getName().equals(goodsSizeName)).findFirst();
                    if (!dictSizePo.isPresent()) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "尺码不存在");
                        success = false;
                        continue;
                    }

                    if (billGoodsList.stream().filter(g -> g.getDetail() != null && g.getGoodsId().equals(goodsPo.getId())
                            && g.getDetail().stream().filter(d -> d.getColorId().equals(goodsColorPo.getColorId()) && d.getSizeId().equals(dictSizePo.get().getId())).count() > 0).count() > 0) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "货号、颜色、内长、尺码在文件中已经存在");
                        success = false;
                    }
                    if (price.compareTo(BigDecimal.ZERO) < 0) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "单价不能小于0");
                        success = false;
                    }
                    if (price.scale() > 2) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "单价最多保留2位小数");
                        success = false;
                    }
                    if (billCount < 1) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "数量必须大于0");
                        success = false;
                    }
                    Optional<BillGoodsVo> billGoodsOptional = billGoodsList.stream().filter(g -> g.getGoodsId().equals(goodsPo.getId())).findFirst();
                    BillGoodsVo billGoodsVo = null;
                    List<BillDetailVo> detailList = null;
                    if (billGoodsOptional.isPresent()) {
                        billGoodsVo = billGoodsOptional.get();
                        detailList = billGoodsVo.getDetail();
                    } else {
                        billGoodsVo = new BillGoodsVo();
                        billGoodsVo.setGoodsCode(goodsPo.getCode());
                        billGoodsVo.setGoodsId(goodsPo.getId());
                        billGoodsVo.setGoodsName(goodsPo.getName());
                        billGoodsVo.setPrice(price);
                        billGoodsVo.setTagPrice(price);
                        billGoodsList.add(billGoodsVo);
                        detailList = new ArrayList<>();
                        billGoodsVo.setDetail(detailList);
                    }
                    BillDetailVo billDetailVo = new BillDetailVo();
                    billDetailVo.setSizeId(dictSizePo.get().getId());
                    billDetailVo.setColorId(goodsColorPo.getColorId());
                    billDetailVo.setBillCount(billCount);
                    detailList.add(billDetailVo);
                } catch (MessageException e) {
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, e.getMessage());
                    success = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
            }

            if (success) {
                billService.save(vo, userSessionBo);
                operations.set(userSessionBo.getId() + ":upload:bill:" + redisKey, new BaseUploadMessage(1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
            } else {
                try (FileOutputStream outputStream = new FileOutputStream(erpFileTempUrl + "/" + userSessionBo.getId() + ".xlsx")) {
                    workbook.write(outputStream);
                    outputStream.flush();
                    operations.set(userSessionBo.getId() + ":upload:bill:" + redisKey, new BaseUploadMessage(userSessionBo.getId() + ".xlsx", -1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void export(BaseBillExportVo vo, HttpServletResponse response, BillService billService, BaseBillGoodsDao baseBillGoodsDao, BaseBillDetailDao baseBillDetailDao,
                       String sourceKey, String targetKey, boolean hasParent) throws Exception {
        vo.setPageIndex(1);
        vo.setPageSize(Integer.MAX_VALUE);
        BackPageVo<BillVo> list = billService.findBillPage(vo);
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("信息表");

        //设置要导出的文件的名字
        String fileName = UUID.randomUUID().toString() + ".xls";
        //新增数据行，并且设置单元格数据

        int rowNum = 1;

        List<String> headers = new ArrayList<>();
        headers.addAll(Arrays.asList(new String[]{"单据编号", "单据时间"}));
        if (hasParent) {
            headers.add("上游单据单号");
        }
        if ("supplier".equals(sourceKey)) {
            headers.addAll(Arrays.asList(new String[]{"供应商编号", "供应商名称"}));
        } else if ("warehouse".equals(sourceKey)) {
            headers.addAll(Arrays.asList(new String[]{"仓库编号", "仓库名称"}));
        } else if ("channel".equals(sourceKey)) {
            headers.addAll(Arrays.asList(new String[]{"渠道编号", "渠道名称"}));
        }
        if (targetKey != null) {
            if ("supplier".equals(targetKey)) {
                headers.addAll(Arrays.asList(new String[]{"供应商编号", "供应商名称"}));
            } else if ("warehouse".equals(targetKey)) {
                headers.addAll(Arrays.asList(new String[]{"仓库编号", "仓库名称"}));
            } else if ("channel".equals(targetKey)) {
                headers.addAll(Arrays.asList(new String[]{"渠道编号", "渠道名称"}));
            }
        }
        headers.addAll(Arrays.asList(new String[]{"总数量", "总金额", "总吊牌价", "创建人", "审核人", "单据状态"}));
        if ("BILL_GOODS".equals(vo.getType())) {
            headers.addAll(Arrays.asList(new String[]{"货号", "货品名称", "单价", "吊牌价", "数量", "金额", "吊牌额"}));
        }
        if ("BILL_DETAIL".equals(vo.getType())) {
            headers.addAll(Arrays.asList(new String[]{"货号", "货品名称", "单价", "吊牌价", "颜色编号", "颜色名称", "内长", "尺码", "数量"}));
        }
        HSSFRow row = sheet.createRow(0);
        int i = 0;
        for (String header : headers) {
            HSSFCell cell = row.createCell(i++);
            HSSFRichTextString text = new HSSFRichTextString(header);
            cell.setCellValue(text);
        }
        if ("BILL".equals(vo.getType())) {
            for (BillVo billVo : list.getContent()) {
                int j = 0;
                HSSFRow row1 = sheet.createRow(rowNum);
                row1.createCell(j++).setCellValue(billVo.getCode());
                row1.createCell(j++).setCellValue(billVo.getBillDate().toString());
                if (hasParent) {
                    row1.createCell(j++).setCellValue(billVo.getParentBillCode());
                }
                if ("supplier".equals(sourceKey)) {
                    row1.createCell(j++).setCellValue(billVo.getSupplierCode());
                    row1.createCell(j++).setCellValue(billVo.getSupplierName());
                } else if ("warehouse".equals(sourceKey)) {
                    row1.createCell(j++).setCellValue(billVo.getWarehouseCode());
                    row1.createCell(j++).setCellValue(billVo.getWarehouseName());
                } else if ("channel".equals(sourceKey)) {
                    row1.createCell(j++).setCellValue(billVo.getChannelCode());
                    row1.createCell(j++).setCellValue(billVo.getChannelName());
                }
                if (targetKey != null) {
                    if ("supplier".equals(targetKey)) {
                        row1.createCell(j++).setCellValue(billVo.getSupplierCode());
                        row1.createCell(j++).setCellValue(billVo.getSupplierName());
                    } else if ("warehouse".equals(targetKey)) {
                        row1.createCell(j++).setCellValue(billVo.getWarehouseCode());
                        row1.createCell(j++).setCellValue(billVo.getWarehouseName());
                    } else if ("channel".equals(targetKey)) {
                        row1.createCell(j++).setCellValue(billVo.getChannelCode());
                        row1.createCell(j++).setCellValue(billVo.getChannelName());
                    }
                }
                row1.createCell(j++).setCellValue(billVo.getTotalCount());
                row1.createCell(j++).setCellValue(billVo.getTotalAmount().toPlainString());
                row1.createCell(j++).setCellValue(billVo.getTotalTagAmount().toPlainString());
                row1.createCell(j++).setCellValue(billVo.getCreateUserName());
                row1.createCell(j++).setCellValue(billVo.getAuditUserName());
                row1.createCell(j++).setCellValue(billVo.getStatusMean());
                rowNum++;
            }
        }
        if ("BILL_GOODS".equals(vo.getType())) {
            for (BillVo billVo : list.getContent()) {
                List<BillGoodsPo> goodsList = baseBillGoodsDao.findByBillId(billVo.getId());
                for (BillGoodsPo goodsPo : goodsList) {
                    int j = 0;
                    HSSFRow row1 = sheet.createRow(rowNum);
                    row1.createCell(j++).setCellValue(billVo.getCode());
                    row1.createCell(j++).setCellValue(billVo.getBillDate().toString());
                    row1.createCell(j++).setCellValue(billVo.getSupplierCode());
                    row1.createCell(j++).setCellValue(billVo.getSupplierName());
                    row1.createCell(j++).setCellValue(billVo.getChannelCode());
                    row1.createCell(j++).setCellValue(billVo.getChannelName());
                    row1.createCell(j++).setCellValue(billVo.getTotalCount());
                    row1.createCell(j++).setCellValue(billVo.getTotalAmount().toPlainString());
                    row1.createCell(j++).setCellValue(billVo.getTotalTagAmount().toPlainString());
                    row1.createCell(j++).setCellValue(billVo.getCreateUserName());
                    row1.createCell(j++).setCellValue(billVo.getAuditUserName());
                    row1.createCell(j++).setCellValue(billVo.getStatusMean());

                    row1.createCell(j++).setCellValue(goodsPo.getGoodsCode());
                    row1.createCell(j++).setCellValue(goodsPo.getGoodsName());
                    row1.createCell(j++).setCellValue(goodsPo.getPrice().toPlainString());
                    row1.createCell(j++).setCellValue(goodsPo.getTagPrice().toPlainString());
                    row1.createCell(j++).setCellValue(goodsPo.getBillCount());
                    row1.createCell(j++).setCellValue(goodsPo.getPrice().multiply(BigDecimal.valueOf(goodsPo.getBillCount())).toPlainString());
                    row1.createCell(j++).setCellValue(goodsPo.getTagPrice().multiply(BigDecimal.valueOf(goodsPo.getBillCount())).toPlainString());
                    rowNum++;
                }
            }
        }
        if ("BILL_DETAIL".equals(vo.getType())) {
            for (BillVo billVo : list.getContent()) {
                List<BillDetailPo> detailList = baseBillDetailDao.findByBillId(billVo.getId());
                for (BillDetailPo detailPo : detailList) {
                    int j = 0;
                    HSSFRow row1 = sheet.createRow(rowNum);
                    row1.createCell(j++).setCellValue(billVo.getCode());
                    row1.createCell(j++).setCellValue(billVo.getBillDate().toString());
                    row1.createCell(j++).setCellValue(billVo.getSupplierCode());
                    row1.createCell(j++).setCellValue(billVo.getSupplierName());
                    row1.createCell(j++).setCellValue(billVo.getChannelCode());
                    row1.createCell(j++).setCellValue(billVo.getChannelName());
                    row1.createCell(j++).setCellValue(billVo.getTotalCount());
                    row1.createCell(j++).setCellValue(billVo.getTotalAmount().toPlainString());
                    row1.createCell(j++).setCellValue(billVo.getTotalTagAmount().toPlainString());
                    row1.createCell(j++).setCellValue(billVo.getCreateUserName());
                    row1.createCell(j++).setCellValue(billVo.getAuditUserName());
                    row1.createCell(j++).setCellValue(billVo.getStatusMean());

                    row1.createCell(j++).setCellValue(detailPo.getGoodsCode());
                    row1.createCell(j++).setCellValue(detailPo.getGoodsName());
                    row1.createCell(j++).setCellValue(detailPo.getPrice().toPlainString());
                    row1.createCell(j++).setCellValue(detailPo.getTagPrice().toPlainString());

                    row1.createCell(j++).setCellValue(detailPo.getGoodsColorCode());
                    row1.createCell(j++).setCellValue(detailPo.getGoodsColorName());
                    row1.createCell(j++).setCellValue(detailPo.getGoodsSizeName());
                    row1.createCell(j++).setCellValue(detailPo.getBillCount());

                    rowNum++;
                }

            }
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        response.flushBuffer();
        workbook.write(response.getOutputStream());
    }

}
