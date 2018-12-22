package com.yin.erp.bill.supplier2warehouse.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.entity.vo.out.BaseUploadMessage;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BillDetailVo;
import com.yin.erp.bill.common.entity.vo.in.BillGoodsVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseDao;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseDetailDao;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseGoodsDao;
import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehouseDetailPo;
import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehouseGoodsPo;
import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehousePo;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.po.DictPo;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.dao.GoodsInSizeDao;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import com.yin.erp.info.goods.entity.po.GoodsInSizePo;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.supplier.dao.SupplierDao;
import com.yin.erp.info.supplier.entity.po.SupplierPo;
import com.yin.erp.info.warehouse.dao.WarehouseDao;
import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import com.yin.erp.stock.service.StockWarehouseService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 厂家来货
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class Supplier2WarehouseService {

    @Autowired
    private Supplier2WarehouseDao supplier2WarehouseDao;
    @Autowired
    private Supplier2WarehouseGoodsDao supplier2WarehouseGoodsDao;
    @Autowired
    private Supplier2WarehouseDetailDao supplier2WarehouseDetailDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsColorDao goodsColorDao;
    @Autowired
    private GoodsInSizeDao goodsInSizeDao;
    @Autowired
    private DictDao dictDao;
    @Autowired
    private DictSizeDao dictSizeDao;
    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StockWarehouseService stockWarehouseService;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;

    /**
     * 查询
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    public BackPageVo<BillVo> findBillPage(SearchBillVo vo) throws MessageException {
        Page<Supplier2WarehousePo> page = supplier2WarehouseDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSupplierCode())) {
                predicates.add(criteriaBuilder.like(root.get("supplierCode"), "%" + vo.getSupplierCode() + "%"));
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
                Predicate p1 = criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%");
                Predicate p2 = criteriaBuilder.like(root.get("createUserName"), "%" + vo.getSearchKey() + "%");
                Predicate p3 = criteriaBuilder.like(root.get("auditUserName"), "%" + vo.getSearchKey() + "%");
                Predicate p4 = criteriaBuilder.like(root.get("supplierCode"), "%" + vo.getSearchKey() + "%");
                Predicate p5 = criteriaBuilder.like(root.get("warehouseCode"), "%" + vo.getSearchKey() + "%");
                Predicate p6 = criteriaBuilder.like(root.get("supplierName"), "%" + vo.getSearchKey() + "%");
                Predicate p7 = criteriaBuilder.like(root.get("warehouseName"), "%" + vo.getSearchKey() + "%");
                Predicate predicatesPermission = criteriaBuilder.or(p1, p2, p3, p4, p5, p6, p7);
                predicates.add(predicatesPermission);
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(vo.getPageIndex() - 1, vo.getPageSize(), Sort.Direction.DESC, "createDate"));
        BackPageVo<BillVo> back = new BackPageVo();
        back.setTotalElements(page.getTotalElements());
        List<BillVo> list = new ArrayList<>();
        for (Supplier2WarehousePo po : page.getContent()) {
            BillVo billVo = new BillVo();
            CopyUtil.copyProperties(billVo, po);
            list.add(billVo);
        }
        back.setContent(list);
        return back;
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    public void save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        Supplier2WarehousePo dbPo = new Supplier2WarehousePo();
        CopyUtil.copyProperties(dbPo, vo);
        if (StringUtils.isBlank(dbPo.getId())) {
            dbPo.setId(GenerateUtil.createUUID());
            dbPo.setCode("CJLH" + GenerateUtil.createSerialNumber());
            dbPo.setCreateUserId(userSessionBo.getId());
            dbPo.setCreateUserName(userSessionBo.getName());
        } else {
            dbPo.setVersion(supplier2WarehouseDao.findById(dbPo.getId()).get().getVersion());
            //删除
            supplier2WarehouseDetailDao.deleteAllByBillId(dbPo.getId());
            supplier2WarehouseGoodsDao.deleteAllByBillId(dbPo.getId());
        }
        dbPo.setTotalCount(0);
        dbPo.setTotalAmount(BigDecimal.ZERO);
        dbPo.setTotalTagAmount(BigDecimal.ZERO);
        WarehousePo warehousePo = warehouseDao.findById(vo.getWarehouseId()).get();
        dbPo.setWarehouseCode(warehousePo.getCode());
        dbPo.setWarehouseName(warehousePo.getName());
        SupplierPo supplierPo = supplierDao.findById(vo.getSupplierId()).get();
        dbPo.setSupplierName(supplierPo.getName());
        dbPo.setSupplierCode(supplierPo.getCode());
        List<Supplier2WarehouseGoodsPo> goodsList = new ArrayList<>();
        List<Supplier2WarehouseDetailPo> detailList = new ArrayList<>();
        for (BillGoodsVo goodsVo : vo.getGoodsList()) {
            GoodsPo goodsPo = goodsDao.findById(goodsVo.getGoodsId()).get();
            Supplier2WarehouseGoodsPo billGoodsPo = new Supplier2WarehouseGoodsPo();
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
                DictPo inSizePo = dictDao.findById(billDetailVo.getInSizeId()).get();
                Supplier2WarehouseDetailPo billDetailPo = new Supplier2WarehouseDetailPo();
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
                billDetailPo.setGoodsInSizeId(inSizePo.getId());
                billDetailPo.setGoodsInSizeName(inSizePo.getName());
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
        supplier2WarehouseDetailDao.saveAll(detailList);
        supplier2WarehouseGoodsDao.saveAll(goodsList);
        supplier2WarehouseDao.saveAndFlush(dbPo);
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            supplier2WarehouseDetailDao.deleteAllByBillId(id);
            supplier2WarehouseGoodsDao.deleteAllByBillId(id);
            supplier2WarehouseDao.deleteById(id);
        }
    }

    /**
     * 审核
     *
     * @param vo
     */
    public void audit(BaseAuditVo vo, UserSessionBo userSessionBo) throws MessageException {
        Date d = new Date();
        for (String id : vo.getIds()) {
            Supplier2WarehousePo po = supplier2WarehouseDao.findById(id).get();
            po.setAuditUserId(userSessionBo.getId());
            po.setAuditUserName(userSessionBo.getName());
            po.setStatus(vo.getStatus());
            po.setAuditDate(d);
            supplier2WarehouseDao.save(po);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : supplier2WarehouseDetailDao.findByBillId(id)) {
                    stockWarehouseService.add(detail, po.getWarehouseId());
                }
            }
        }
        supplier2WarehouseDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Supplier2WarehousePo po = supplier2WarehouseDao.findById(id).get();
            po.setStatus("AUDIT_FAILURE");
            supplier2WarehouseDao.save(po);
            for (BillDetailPo detail : supplier2WarehouseDetailDao.findByBillId(id)) {
                stockWarehouseService.minus(detail, po.getWarehouseId());
            }
        }
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BillVo findById(String id) throws MessageException {
        Supplier2WarehousePo po = supplier2WarehouseDao.findById(id).get();
        BillVo vo = new BillVo();
        CopyUtil.copyProperties(vo, po);
        List<BillGoodsVo> list = new ArrayList<>();
        List<Supplier2WarehouseGoodsPo> goodsList = supplier2WarehouseGoodsDao.findByBillId(id);
        List<Supplier2WarehouseDetailPo> detailList = supplier2WarehouseDetailDao.findByBillId(id);
        for (Supplier2WarehouseGoodsPo goodsPo : goodsList) {
            BillGoodsVo goodsVo = new BillGoodsVo();
            CopyUtil.copyProperties(goodsVo, goodsPo);
            List<BillDetailVo> detail = new ArrayList<>();
            for (Supplier2WarehouseDetailPo detailPo : detailList.stream().filter(d -> d.getBillGoodsId().equals(goodsPo.getId())).collect(Collectors.toList())) {
                BillDetailVo billDetailVo = new BillDetailVo();
                CopyUtil.copyProperties(billDetailVo, detailPo);
                billDetailVo.setColorId(detailPo.getGoodsColorId());
                billDetailVo.setInSizeId(detailPo.getGoodsInSizeId());
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
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
//    @Async
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
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
                operations.set(userSessionBo.getId() + ":upload:bill", new BaseUploadMessage(sheet.getLastRowNum() + 1, count), 10L, TimeUnit.MINUTES);
                if (count == 1) {
                    row.createCell(errorCellNum).setCellValue("错误信息");
                    continue;
                }
                try {
                    //获取数据
                    Date date = ExcelReadUtil.getDate(row.getCell(0));
                    String supplierCode = ExcelReadUtil.getString(row.getCell(1));
                    String warehouseCode = ExcelReadUtil.getString(row.getCell(2));
                    String goodsCode = ExcelReadUtil.getString(row.getCell(3));
                    String goodsColorCode = ExcelReadUtil.getString(row.getCell(4));
                    String goodsColorName = ExcelReadUtil.getString(row.getCell(5));
                    String goodsInSizeName = ExcelReadUtil.getString(row.getCell(6));
                    String goodsSizeName = ExcelReadUtil.getString(row.getCell(7));
                    BigDecimal price = ExcelReadUtil.getBigDecimal(row.getCell(8));
                    Integer billCount = ExcelReadUtil.getInteger(row.getCell(9));

                    if (date == null | StringUtils.isBlank(supplierCode) || StringUtils.isBlank(warehouseCode)
                            || StringUtils.isBlank(goodsCode) || StringUtils.isBlank(goodsColorCode) || StringUtils.isBlank(goodsColorName)
                            || StringUtils.isBlank(goodsInSizeName) || StringUtils.isBlank(goodsSizeName) || billCount == null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "缺少必要数据");
                        success = false;
                        continue;
                    }

                    if (vo.getBillDate() == null) {
                        vo.setBillDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    }
                    if (vo.getSupplierId() == null) {
                        SupplierPo supplierPo = supplierDao.findByCode(supplierCode);
                        if (supplierPo == null) {
                            ExcelReadUtil.addErrorToRow(row, errorCellNum, "供应商没找到");
                            success = false;
                        }
                        vo.setSupplierId(supplierPo.getId());
                    }
                    if (vo.getWarehouseId() == null) {
                        WarehousePo warehousePo = warehouseDao.findByCode(warehouseCode);
                        if (warehousePo == null) {
                            ExcelReadUtil.addErrorToRow(row, errorCellNum, "仓库没找到");
                            success = false;
                        }
                        vo.setWarehouseId(warehousePo.getId());
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
                    List<GoodsInSizePo> inSizeList = goodsInSizeDao.findByGoodsId(goodsPo.getId());
                    GoodsInSizePo goodsInSizePo = inSizeList.stream().filter(g -> g.getInSizeName().equals(goodsInSizeName)).findFirst().get();
                    if (goodsInSizePo == null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "内长不存在");
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
                            && g.getDetail().stream().filter(d -> d.getColorId().equals(goodsColorPo.getColorId()) && d.getInSizeId().equals(goodsInSizePo.getInSizeId()) && d.getSizeId().equals(dictSizePo.get().getId())).count() > 0).count() > 0) {
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
                    billDetailVo.setInSizeId(goodsInSizePo.getInSizeId());
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
                this.save(vo, userSessionBo);
                operations.set(userSessionBo.getId() + ":upload:bill", new BaseUploadMessage(1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
            } else {
                try (FileOutputStream outputStream = new FileOutputStream(erpFileTempUrl + "/" + userSessionBo.getId() + ".xlsx")) {
                    workbook.write(outputStream);
                    outputStream.flush();
                    operations.set(userSessionBo.getId() + ":upload:bill", new BaseUploadMessage(userSessionBo.getId() + ".xlsx", -1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
