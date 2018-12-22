package com.yin.erp.bill.warehouse2channel.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BillDetailVo;
import com.yin.erp.bill.common.entity.vo.in.BillGoodsVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDetailDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelGoodsDao;
import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelDetailPo;
import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelGoodsPo;
import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelPo;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 仓库出货
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class Warehouse2ChannelService {

    @Autowired
    private Warehouse2ChannelDao supplier2WarehouseDao;
    @Autowired
    private Warehouse2ChannelGoodsDao supplier2WarehouseGoodsDao;
    @Autowired
    private Warehouse2ChannelDetailDao supplier2WarehouseDetailDao;
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
    @Value("erp.bill.download.url")
    private String billDownUrl;

    /**
     * 查询
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    public BackPageVo<BillVo> findBillPage(SearchBillVo vo) throws MessageException {
        Page<Warehouse2ChannelPo> page = supplier2WarehouseDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
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
        for (Warehouse2ChannelPo po : page.getContent()) {
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
        Warehouse2ChannelPo dbPo = new Warehouse2ChannelPo();
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
        //TODO
//        SupplierPo supplierPo = supplierDao.findById(vo.getSupplierId()).get();
//        dbPo.setSupplierName(supplierPo.getName());
//        dbPo.setSupplierCode(supplierPo.getCode());
        List<Warehouse2ChannelGoodsPo> goodsList = new ArrayList<>();
        for (BillGoodsVo goodsVo : vo.getGoodsList()) {
            GoodsPo goodsPo = goodsDao.findById(goodsVo.getGoodsId()).get();
            Warehouse2ChannelGoodsPo billGoodsPo = new Warehouse2ChannelGoodsPo();
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
                Warehouse2ChannelDetailPo billDetailPo = new Warehouse2ChannelDetailPo();
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
                supplier2WarehouseDetailDao.save(billDetailPo);
                //求和
                billGoodsPo.setBillCount(billGoodsPo.getBillCount() + billDetailVo.getBillCount());
            }
            goodsList.add(billGoodsPo);
            //求和
            dbPo.setTotalCount(dbPo.getTotalCount() + billGoodsPo.getBillCount());
            dbPo.setTotalAmount(dbPo.getTotalAmount().add(billGoodsPo.getPrice().multiply(BigDecimal.valueOf(billGoodsPo.getBillCount()))));
            dbPo.setTotalTagAmount(dbPo.getTotalTagAmount().add(billGoodsPo.getTagPrice().multiply(BigDecimal.valueOf(billGoodsPo.getBillCount()))));
        }
        supplier2WarehouseGoodsDao.saveAll(goodsList);
        supplier2WarehouseDao.save(dbPo);
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
    public void audit(BaseAuditVo vo, UserSessionBo userSessionBo) {
        Date d = new Date();
        for (String id : vo.getIds()) {
            Warehouse2ChannelPo po = supplier2WarehouseDao.findById(id).get();
            po.setAuditUserId(userSessionBo.getId());
            po.setAuditUserName(userSessionBo.getName());
            po.setStatus(vo.getStatus());
            po.setAuditDate(d);
            supplier2WarehouseDao.save(po);
        }
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            Warehouse2ChannelPo po = supplier2WarehouseDao.findById(id).get();
            po.setStatus("AUDIT_FAILURE");
            supplier2WarehouseDao.save(po);
        }
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BillVo findById(String id) throws MessageException {
        Warehouse2ChannelPo po = supplier2WarehouseDao.findById(id).get();
        BillVo vo = new BillVo();
        CopyUtil.copyProperties(vo, po);
        List<BillGoodsVo> list = new ArrayList<>();
        List<Warehouse2ChannelGoodsPo> goodsList = supplier2WarehouseGoodsDao.findByBillId(id);
        List<Warehouse2ChannelDetailPo> detailList = supplier2WarehouseDetailDao.findByBillId(id);
        for (Warehouse2ChannelGoodsPo goodsPo : goodsList) {
            BillGoodsVo goodsVo = new BillGoodsVo();
            CopyUtil.copyProperties(goodsVo, goodsPo);
            List<BillDetailVo> detail = new ArrayList<>();
            for (Warehouse2ChannelDetailPo detailPo : detailList.stream().filter(d -> d.getBillGoodsId().equals(goodsPo.getId())).collect(Collectors.toList())) {
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

    @Async
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int count = 0;
            //准备数据
            BillVo vo = new BillVo();
            vo.setStatus("PENDING");
            List<BillGoodsVo> billGoodsList = new ArrayList<>();
            vo.setGoodsList(billGoodsList);
            List<Map<String, Object>> list = new ArrayList();
            for (Row row : sheet) {
                count++;
                if (count == 1) {
                    continue;
                }
                Date date;
                String supplierCode, warehouseCode, goodsCode = null;
                BigDecimal price;
                Integer billCount;
                try {
                    //获取数据
                    date = ExcelReadUtil.getDate(row.getCell(0));
                    supplierCode = ExcelReadUtil.getString(row.getCell(1));
                    warehouseCode = ExcelReadUtil.getString(row.getCell(2));
                    goodsCode = ExcelReadUtil.getString(row.getCell(3));
                    String goodsColorCode = ExcelReadUtil.getString(row.getCell(4));
                    String goodsColorName = ExcelReadUtil.getString(row.getCell(5));
                    String goodsInSizeName = ExcelReadUtil.getString(row.getCell(6));
                    String goodsSizeName = ExcelReadUtil.getString(row.getCell(7));
                    price = ExcelReadUtil.getBigDecimal(row.getCell(8));
                    billCount = ExcelReadUtil.getInteger(row.getCell(9));

                    Map outMap = ExcelReadUtil.createOutMap(date, supplierCode, warehouseCode, goodsCode, goodsColorCode, goodsColorName, goodsInSizeName, goodsSizeName, price, billCount);

                    if (vo.getBillDate() == null) {
                        vo.setBillDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                    }
                    if (vo.getSupplierId() == null) {
                        SupplierPo supplierPo = supplierDao.findByCode(supplierCode);
                        if (supplierPo == null) {
                            ExcelReadUtil.addErrorMap(outMap, "供应商没找到");
                            continue;
                        }
                        vo.setSupplierId(supplierPo.getId());
                    }
                    if (vo.getWarehouseId() == null) {
                        WarehousePo warehousePo = warehouseDao.findByCode(warehouseCode);
                        if (warehousePo == null) {
                            ExcelReadUtil.addErrorMap(outMap, "仓库没找到");
                            continue;
                        }
                        vo.setWarehouseId(warehousePo.getId());
                    }

                    //判断是否匹配货号、颜色、内长、尺码
                    GoodsPo goodsPo = goodsDao.findByCode(goodsCode);
                    if (goodsPo == null) {
                        ExcelReadUtil.addErrorMap(outMap, "货号不存在");
                        continue;
                    }
                    List<GoodsColorPo> colorList = goodsColorDao.findByGoodsId(goodsPo.getId());
                    GoodsColorPo goodsColorPo = colorList.stream().filter(g -> g.getColorCode().equals(goodsColorCode) && g.getColorName().equals(goodsColorName)).findFirst().get();
                    if (goodsColorPo == null) {
                        ExcelReadUtil.addErrorMap(outMap, "颜色编号和名称没定位颜色");
                        continue;
                    }
                    List<GoodsInSizePo> inSizeList = goodsInSizeDao.findByGoodsId(goodsPo.getId());
                    GoodsInSizePo goodsInSizePo = inSizeList.stream().filter(g -> g.getInSizeName().equals(goodsInSizeName)).findFirst().get();
                    if (goodsInSizePo == null) {
                        ExcelReadUtil.addErrorMap(outMap, "内长不存在");
                        continue;
                    }
                    //尺码
                    List<DictSizePo> sizeList = dictSizeDao.findByGroupId(goodsPo.getSizeGroupId());
                    Optional<DictSizePo> dictSizePo = sizeList.stream().filter(g -> g.getName().equals(goodsSizeName)).findFirst();
                    if (!dictSizePo.isPresent()) {
                        ExcelReadUtil.addErrorMap(outMap, "尺码不存在");
                        continue;
                    }
                    if (list.stream().filter(m -> m.get("goodsColorCode").equals("goodsColorCode") && m.get("goodsColorName").equals("goodsColorName")
                            && m.get("goodsInSizeName").equals("goodsInSizeName") && m.get("goodsSizeName").equals("goodsSizeName")).count() > 0) {
                        ExcelReadUtil.addErrorMap(outMap, "货号、颜色、内长、尺码在文件中已经存在");
                        continue;
                    }
                    if (price.compareTo(BigDecimal.ZERO) < 0) {
                        ExcelReadUtil.addErrorMap(outMap, "单价不能小于0");
                        continue;
                    }
                    if (price.scale() > 2) {
                        ExcelReadUtil.addErrorMap(outMap, "单价最多保留2位小数");
                        continue;
                    }
                    if (billCount < 1) {
                        ExcelReadUtil.addErrorMap(outMap, "数量必须大于0");
                        continue;
                    }
                    list.add(outMap);
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
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (list.stream().filter(r -> !r.get(r.size() - 1).equals("")).count() > 0) {
                try (FileOutputStream outputStream = new FileOutputStream(billDownUrl + userSessionBo.getId() + ".xlsx")) {
                    workbook.write(outputStream);
                    outputStream.flush();
                } catch (Exception e) {

                }
                return;
            }
            this.save(vo, userSessionBo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
