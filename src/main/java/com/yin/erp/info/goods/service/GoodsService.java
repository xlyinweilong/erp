package com.yin.erp.info.goods.service;

import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BaseUploadMessage;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.info.dict.dao.DictDao;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.bo.DictSizeBo;
import com.yin.erp.info.dict.entity.po.DictPo;
import com.yin.erp.info.dict.enums.DictGoodsType;
import com.yin.erp.info.dict.enums.DictType;
import com.yin.erp.info.dict.feign.DictFeign;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.dao.GoodsInSizeDao;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import com.yin.erp.info.goods.entity.po.GoodsInSizePo;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.goods.entity.vo.GoodsVo;
import com.yin.erp.info.supplier.dao.SupplierDao;
import com.yin.erp.info.supplier.entity.po.SupplierPo;
import com.yin.erp.info.supplier.feign.SupplierFeign;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 货品资料服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsColorDao goodsColorDao;
    @Autowired
    private GoodsInSizeDao goodsInSizeDao;
    @Autowired
    private DictFeign dictFeign;
    @Autowired
    private SupplierFeign supplierFeign;
    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private DictSizeDao dictSizeDao;
    @Autowired
    private DictDao dictDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;

    /**
     * 保存
     *
     * @param vo
     * @throws Exception
     */
    public void save(GoodsVo vo) throws MessageException {
        GoodsPo po = new GoodsPo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = goodsDao.findById(vo.getId()).get();
            goodsColorDao.deleteByGoodsId(vo.getId());
            goodsInSizeDao.deleteByGoodsId(vo.getId());
            //发送给队列，全局做数据更新 TODO
        }
        po.setCode(vo.getCode());
        po.setName(vo.getName());
        po.setBrandId(vo.getBrandId());
        po.setBrandName(dictFeign.getNameById(vo.getBrandId()));
        po.setCategory2Id(vo.getCategory2Id());
        po.setCategory2Name(dictFeign.getNameById(vo.getCategory2Id()));
        po.setCategoryId(vo.getCategoryId());
        po.setCategoryName(dictFeign.getNameById(vo.getCategoryId()));
        po.setPatternId(vo.getPatternId());
        po.setPatternName(dictFeign.getNameById(vo.getPatternId()));
        po.setSeasonId(vo.getSeasonId());
        po.setSeasonName(dictFeign.getNameById(vo.getSeasonId()));
        po.setSeriesId(vo.getSeriesId());
        po.setSeriesName(dictFeign.getNameById(vo.getSeriesId()));
        po.setSizeGroupId(vo.getSizeGroupId());
        po.setSizeGroupName(dictFeign.getNameById(vo.getSizeGroupId()));
        po.setYearId(vo.getYearId());
        po.setYearName(dictFeign.getNameById(vo.getYearId()));
        po.setSupplierId(vo.getSupplierId());
        po.setSupplierCode(supplierFeign.getCodeById(vo.getSupplierId()));
        po.setSupplierName(supplierFeign.getNameById(vo.getSupplierId()));
        po.setStyleId(vo.getStyleId());
        po.setStyleName(dictFeign.getNameById(vo.getStyleId()));
        po.setSexId(vo.getSexId());
        po.setSexName(dictFeign.getNameById(vo.getSexId()));
        po.setTagPrice1(vo.getTagPrice1());
        goodsDao.save(po);
        //货品颜色
        for (String colorId : vo.getColorIdList()) {
            GoodsColorPo goodsColorPo = new GoodsColorPo();
            goodsColorPo.setColorId(colorId);
            goodsColorPo.setColorCode(dictFeign.getCodeById(colorId));
            goodsColorPo.setColorName(dictFeign.getNameById(colorId));
            goodsColorPo.setGoodsId(po.getId());
            goodsColorDao.save(goodsColorPo);
        }
        //货品内长
        for (String inSizeId : vo.getInSizeIdList()) {
            GoodsInSizePo goodsInSizePo = new GoodsInSizePo();
            goodsInSizePo.setInSizeId(inSizeId);
            goodsInSizePo.setInSizeName(dictFeign.getNameById(inSizeId));
            goodsInSizePo.setGoodsId(po.getId());
            goodsInSizeDao.save(goodsInSizePo);
        }

    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public GoodsVo findById(String id) {
        GoodsPo dictPo = goodsDao.findById(id).get();
        GoodsVo dictVo = new GoodsVo();
        dictVo.setId(dictPo.getId());
        dictVo.setCode(dictPo.getCode());
        dictVo.setName(dictPo.getName());
        dictVo.setSizeGroupId(dictPo.getSizeGroupId());
        dictVo.setSizeGroupName(dictPo.getSizeGroupName());
        dictVo.setBrandId(dictPo.getBrandId());
        dictVo.setBrandName(dictPo.getBrandName());
        dictVo.setCategory2Id(dictPo.getCategory2Id());
        dictVo.setCategory2Name(dictPo.getCategory2Name());
        dictVo.setCategoryId(dictPo.getCategoryId());
        dictVo.setCategoryName(dictPo.getCategoryName());
        dictVo.setPatternId(dictPo.getPatternId());
        dictVo.setPatternName(dictPo.getPatternName());
        dictVo.setYearId(dictPo.getYearId());
        dictVo.setYearName(dictPo.getYearName());
        dictVo.setSexId(dictPo.getSexId());
        dictVo.setSexName(dictPo.getSexName());
        dictVo.setStyleId(dictPo.getStyleId());
        dictVo.setStyleName(dictPo.getStyleName());
        dictVo.setSeasonId(dictPo.getSeasonId());
        dictVo.setSeasonName(dictPo.getSeasonName());
        dictVo.setSeriesId(dictPo.getSeriesId());
        dictVo.setSeriesName(dictPo.getSeriesName());
        dictVo.setSupplierCode(dictPo.getSupplierCode());
        dictVo.setSupplierId(dictPo.getSupplierId());
        dictVo.setSupplierName(dictPo.getSupplierName());
        dictVo.setTagPrice1(dictPo.getTagPrice1());
        dictVo.setColorList(goodsColorDao.findByGoodsId(dictPo.getId()));
        dictVo.setInSizeList(goodsInSizeDao.findByGoodsId(dictPo.getId()));
        return dictVo;
    }

    /**
     * 查询货品
     *
     * @param vo
     * @return
     */
    public Page<GoodsPo> findGoodsPage(GoodsVo vo) {
        Page<GoodsPo> page = goodsDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getName())) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + vo.getName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSizeGroupName())) {
                predicates.add(criteriaBuilder.like(root.get("sizeGroupName"), "%" + vo.getSizeGroupName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getBrandName())) {
                predicates.add(criteriaBuilder.like(root.get("brandName"), "%" + vo.getBrandName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getCategoryName())) {
                predicates.add(criteriaBuilder.like(root.get("categoryName"), "%" + vo.getCategoryName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getCategory2Name())) {
                predicates.add(criteriaBuilder.like(root.get("category2Name"), "%" + vo.getCategory2Name() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSeriesName())) {
                predicates.add(criteriaBuilder.like(root.get("seriesName"), "%" + vo.getSeriesName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getPatternName())) {
                predicates.add(criteriaBuilder.like(root.get("patternName"), "%" + vo.getPatternName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getStyleName())) {
                predicates.add(criteriaBuilder.like(root.get("styleName"), "%" + vo.getStyleName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSeasonName())) {
                predicates.add(criteriaBuilder.like(root.get("seasonName"), "%" + vo.getSeasonName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getYearName())) {
                predicates.add(criteriaBuilder.like(root.get("yearName"), "%" + vo.getYearName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSexName())) {
                predicates.add(criteriaBuilder.like(root.get("sexName"), "%" + vo.getSexName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSupplierCode())) {
                predicates.add(criteriaBuilder.like(root.get("supplierCode"), "%" + vo.getSupplierCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSupplierName())) {
                predicates.add(criteriaBuilder.like(root.get("supplierName"), "%" + vo.getSupplierName() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getSearchKey())) {
                Predicate p1 = criteriaBuilder.like(root.get("code"), "%" + vo.getSearchKey() + "%");
                Predicate p2 = criteriaBuilder.like(root.get("name"), "%" + vo.getSearchKey() + "%");
                Predicate p3 = criteriaBuilder.like(root.get("sizeGroupName"), "%" + vo.getSearchKey() + "%");
                Predicate p4 = criteriaBuilder.like(root.get("brandName"), "%" + vo.getSearchKey() + "%");
                Predicate p5 = criteriaBuilder.like(root.get("categoryName"), "%" + vo.getSearchKey() + "%");
                Predicate p6 = criteriaBuilder.like(root.get("category2Name"), "%" + vo.getSearchKey() + "%");
                Predicate p7 = criteriaBuilder.like(root.get("seriesName"), "%" + vo.getSearchKey() + "%");
                Predicate p8 = criteriaBuilder.like(root.get("patternName"), "%" + vo.getSearchKey() + "%");
                Predicate p9 = criteriaBuilder.like(root.get("styleName"), "%" + vo.getSearchKey() + "%");
                Predicate p10 = criteriaBuilder.like(root.get("seasonName"), "%" + vo.getSearchKey() + "%");
                Predicate p11 = criteriaBuilder.like(root.get("yearName"), "%" + vo.getSearchKey() + "%");
                Predicate p12 = criteriaBuilder.like(root.get("sexName"), "%" + vo.getSearchKey() + "%");
                Predicate p13 = criteriaBuilder.like(root.get("supplierCode"), "%" + vo.getSearchKey() + "%");
                Predicate p14 = criteriaBuilder.like(root.get("supplierName"), "%" + vo.getSearchKey() + "%");
                Predicate predicatesPermission = criteriaBuilder.or(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14);
                predicates.add(predicatesPermission);
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
            this.deleteById(id);
        }
    }

    /**
     * 根据ID删除
     *
     * @param id
     */
    public void deleteById(String id) {
        //查询货品/渠道引用情况 TODO
        goodsColorDao.deleteByGoodsId(id);
        goodsInSizeDao.deleteByGoodsId(id);
        goodsDao.deleteById(id);
    }

    /**
     * 根据货号查询获得的颜色、内长、尺码列表
     *
     * @param id
     * @return
     */
    public GoodsVo getGoodsColorAndInSizeAndSizeList(String id) {
        GoodsVo vo = new GoodsVo();
        GoodsPo goodsPo = goodsDao.findById(id).get();
        List<DictSizeBo> sizeList = dictFeign.findDictSizePo(goodsPo.getSizeGroupId());
        vo.setSizeList(sizeList);
        vo.setColorList(goodsColorDao.findByGoodsId(id));
        vo.setInSizeList(goodsInSizeDao.findByGoodsId(id));
        return vo;
    }

    /**
     * 导入货品资料
     *
     * @param file
     * @param userSessionBo
     */
    @Async
    public void updateGoods(MultipartFile file, UserSessionBo userSessionBo) {
        ValueOperations operations = redisTemplate.opsForValue();
        LocalDateTime startTime = LocalDateTime.now();
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int count = 0;
            List<GoodsPo> goodsList = new ArrayList();
            List<GoodsInSizePo> goodsInSizeList = new ArrayList<>();
            List<GoodsColorPo> goodsColorList = new ArrayList<>();
            int errorCellNum = 17;
            boolean success = true;
            for (Row row : sheet) {
                count++;
                operations.set(userSessionBo.getId() + ":upload:goods", new BaseUploadMessage(sheet.getLastRowNum() + 1, count), 10L, TimeUnit.MINUTES);
                if (count == 1) {
                    row.createCell(errorCellNum).setCellValue("错误信息");
                    continue;
                }
                try {
                    //获取数据
                    String code = ExcelReadUtil.getString(row.getCell(0));
                    String name = ExcelReadUtil.getString(row.getCell(1));
                    String sizeGroupName = ExcelReadUtil.getString(row.getCell(2));
                    String colorCode = ExcelReadUtil.getString(row.getCell(3));
                    String colorName = ExcelReadUtil.getString(row.getCell(4));
                    String inSizeName = ExcelReadUtil.getString(row.getCell(5));
                    String brandName = ExcelReadUtil.getString(row.getCell(6));
                    String categoryName = ExcelReadUtil.getString(row.getCell(7));
                    String category2Name = ExcelReadUtil.getString(row.getCell(8));
                    String seriesName = ExcelReadUtil.getString(row.getCell(9));
                    String patternName = ExcelReadUtil.getString(row.getCell(10));
                    String styleName = ExcelReadUtil.getString(row.getCell(11));
                    String seasonName = ExcelReadUtil.getString(row.getCell(12));
                    String yearName = ExcelReadUtil.getString(row.getCell(13));
                    String sexName = ExcelReadUtil.getString(row.getCell(14));
                    String supplierCode = ExcelReadUtil.getString(row.getCell(15));
                    BigDecimal tagPrice1 = ExcelReadUtil.getBigDecimal(row.getCell(16));

                    if (StringUtils.isBlank(code) || StringUtils.isBlank(name) || StringUtils.isBlank(sizeGroupName) || StringUtils.isBlank(colorCode) || StringUtils.isBlank(colorName)
                            || StringUtils.isBlank(inSizeName) || tagPrice1 == null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "缺少必要数据");
                        success = false;
                        continue;
                    }

                    //判断货号是否存在，存在删除
                    GoodsPo g = goodsDao.findByCode(code);
                    if (g != null) {
                        this.deleteById(g.getId());
                    }

                    GoodsPo goodsPo = new GoodsPo();
                    goodsPo.setCode(code);
                    goodsPo.setName(name);
                    goodsPo.setId(GenerateUtil.createUUID());
                    goodsPo.setTagPrice1(tagPrice1);
                    if (tagPrice1.compareTo(BigDecimal.ZERO) < 0) {
                        success = false;
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "吊牌价必须不能小于0");
                    }
                    if (tagPrice1.scale() > 2) {
                        success = false;
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "吊牌价最多只能有2位小数");
                    }
                    if (goodsList.contains(goodsPo)) {
                        goodsPo.setId(goodsList.get(goodsList.indexOf(goodsPo)).getId());
                    }

                    GoodsInSizePo goodsInSizePo = new GoodsInSizePo();
                    GoodsColorPo goodsColorPo = new GoodsColorPo();

                    DictPo dictSizePo = dictDao.findByNameAndType1AndType2(sizeGroupName, DictType.GOODS.name(), DictGoodsType.SIZE_GROUP.name());
                    if (dictSizePo == null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "尺码组不存在");
                        success = false;
                        continue;
                    }
                    goodsPo.setSizeGroupId(dictSizePo.getId());
                    goodsPo.setSizeGroupName(dictSizePo.getName());

                    //供应商
                    if (StringUtils.isNotBlank(supplierCode)) {
                        SupplierPo supplierPo = supplierDao.findByCode(supplierCode);
                        if (supplierPo == null) {
                            ExcelReadUtil.addErrorToRow(row, errorCellNum, "供应商不存在");
                            success = false;
                            continue;
                        }
                        goodsPo.setSupplierId(supplierPo.getId());
                        goodsPo.setSupplierCode(supplierPo.getCode());
                        goodsPo.setSupplierName(supplierPo.getName());
                    }

                    //插入颜色
                    DictPo dictColor = dictDao.findByCodeAndNameAndType1AndType2(colorCode, colorName, DictType.GOODS.name(), DictGoodsType.COLOR.name());
                    if (dictColor == null) {
                        dictColor = new DictPo(colorCode, colorName, DictType.GOODS.name(), DictGoodsType.COLOR.name());
                        dictDao.save(dictColor);
                    }
                    goodsColorPo.setColorId(dictColor.getId());
                    goodsColorPo.setColorCode(dictColor.getCode());
                    goodsColorPo.setColorName(dictColor.getName());
                    goodsColorPo.setGoodsId(goodsPo.getId());
                    if (goodsColorList.contains(goodsColorPo)) {

                    } else {
                        goodsColorList.add(goodsColorPo);
                    }

                    //内长
                    DictPo dictInSize = dictDao.findByNameAndType1AndType2(inSizeName, DictType.GOODS.name(), DictGoodsType.IN_SIZE.name());
                    if (dictInSize == null) {
                        dictInSize = new DictPo(inSizeName, DictType.GOODS.name(), DictGoodsType.IN_SIZE.name());
                        dictDao.save(dictInSize);
                    }
                    goodsInSizePo.setGoodsId(goodsPo.getId());
                    goodsInSizePo.setInSizeId(dictInSize.getId());
                    goodsInSizePo.setInSizeName(dictInSize.getName());
                    if (goodsInSizeList.contains(goodsInSizePo)) {

                    } else {
                        goodsInSizeList.add(goodsInSizePo);
                    }

                    //品牌
                    if (StringUtils.isNotBlank(brandName)) {
                        DictPo dictBrand = this.insertDictPo4UploadGoods(brandName, DictGoodsType.BRAND);
                        goodsPo.setBrandId(dictBrand.getId());
                        goodsPo.setBrandName(dictBrand.getName());
                    }

                    //品类
                    if (StringUtils.isNotBlank(categoryName)) {
                        DictPo dictCategoryName = this.insertDictPo4UploadGoods(categoryName, DictGoodsType.CATEGORY);
                        goodsPo.setCategoryId(dictCategoryName.getId());
                        goodsPo.setCategoryName(dictCategoryName.getName());
                    }

                    //二级品类
                    if (StringUtils.isNotBlank(category2Name)) {
                        DictPo dictCategory2Name = this.insertDictPo4UploadGoods(category2Name, DictGoodsType.CATEGORY_2);
                        goodsPo.setCategory2Id(dictCategory2Name.getId());
                        goodsPo.setCategory2Name(dictCategory2Name.getName());
                    }

                    //系列
                    if (StringUtils.isNotBlank(seriesName)) {
                        DictPo dictSeries = this.insertDictPo4UploadGoods(seriesName, DictGoodsType.SERIES);
                        goodsPo.setSeriesId(dictSeries.getId());
                        goodsPo.setSeriesName(dictSeries.getName());
                    }

                    //款式
                    if (StringUtils.isNotBlank(patternName)) {
                        DictPo dictPattern = this.insertDictPo4UploadGoods(patternName, DictGoodsType.PATTERN);
                        goodsPo.setPatternId(dictPattern.getId());
                        goodsPo.setPatternName(dictPattern.getName());
                    }

                    //风格
                    if (StringUtils.isNotBlank(styleName)) {
                        DictPo dictStyle = this.insertDictPo4UploadGoods(styleName, DictGoodsType.STYLE);
                        goodsPo.setStyleId(dictStyle.getId());
                        goodsPo.setStyleName(dictStyle.getName());
                    }

                    //季节
                    if (StringUtils.isNotBlank(seasonName)) {
                        DictPo dictSeason = this.insertDictPo4UploadGoods(seasonName, DictGoodsType.SEASON);
                        goodsPo.setSeasonId(dictSeason.getId());
                        goodsPo.setSeasonName(dictSeason.getName());
                    }

                    //年份
                    if (StringUtils.isNotBlank(yearName)) {
                        DictPo dictYear = this.insertDictPo4UploadGoods(yearName, DictGoodsType.YEAR);
                        goodsPo.setYearId(dictYear.getId());
                        goodsPo.setYearName(dictYear.getName());
                    }

                    //性别
                    if (StringUtils.isNotBlank(sexName)) {
                        DictPo dictSex = this.insertDictPo4UploadGoods(sexName, DictGoodsType.SEX);
                        goodsPo.setSexId(dictSex.getId());
                        goodsPo.setSexName(dictSex.getName());
                    }

                    if (!goodsList.contains(goodsPo)) {
                        goodsList.add(goodsPo);
                    }
                } catch (MessageException e) {
                    success = false;
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, e.getMessage());
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (success) {
                goodsDao.saveAll(goodsList);
                goodsInSizeDao.saveAll(goodsInSizeList);
                goodsColorDao.saveAll(goodsColorList);
                operations.set(userSessionBo.getId() + ":upload:goods", new BaseUploadMessage(1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
            } else {
                try (FileOutputStream outputStream = new FileOutputStream(erpFileTempUrl + "/" + userSessionBo.getId() + ".xlsx")) {
                    workbook.write(outputStream);
                    outputStream.flush();
                    operations.set(userSessionBo.getId() + ":upload:goods", new BaseUploadMessage(userSessionBo.getId() + ".xlsx", -1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            operations.set(userSessionBo.getId() + ":upload:goods", "", 1L);
        }
    }

    /**
     * 查询存在返回、没有插入
     *
     * @param name
     * @param dictGoodsType
     * @return
     */
    private DictPo insertDictPo4UploadGoods(String name, DictGoodsType dictGoodsType) {
        DictPo dictPo = dictDao.findByNameAndType1AndType2(name, DictType.GOODS.name(), dictGoodsType.name());
        if (dictPo == null) {
            dictPo = new DictPo(name, DictType.GOODS.name(), dictGoodsType.name());
            dictDao.save(dictPo);
        }
        return dictPo;
    }

    /**
     * 查询数量
     *
     * @param vo
     * @return
     */
    public long findCount(GoodsVo vo) {
        return goodsDao.count((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.equal(root.get("code"), vo.getCode()));
            }
            if (StringUtils.isNoneBlank(vo.getName())) {
                predicates.add(criteriaBuilder.equal(root.get("name"), vo.getName()));
            }
            if (StringUtils.isNoneBlank(vo.getSizeGroupId())) {
                predicates.add(criteriaBuilder.equal(root.get("sizeGroupId"), vo.getSizeGroupId()));
            }
            if (StringUtils.isNoneBlank(vo.getBrandId())) {
                predicates.add(criteriaBuilder.equal(root.get("brandId"), vo.getBrandId()));
            }
            if (StringUtils.isNoneBlank(vo.getCategoryId())) {
                predicates.add(criteriaBuilder.equal(root.get("categoryId"), vo.getCategoryId()));
            }
            if (StringUtils.isNoneBlank(vo.getCategory2Id())) {
                predicates.add(criteriaBuilder.equal(root.get("category2Id"), vo.getCategory2Id()));
            }
            if (StringUtils.isNoneBlank(vo.getSeriesId())) {
                predicates.add(criteriaBuilder.equal(root.get("seriesId"), vo.getSeriesId()));
            }
            if (StringUtils.isNoneBlank(vo.getPatternId())) {
                predicates.add(criteriaBuilder.equal(root.get("patternId"), vo.getPatternId()));
            }
            if (StringUtils.isNoneBlank(vo.getStyleId())) {
                predicates.add(criteriaBuilder.equal(root.get("styleId"), vo.getStyleId()));
            }
            if (StringUtils.isNoneBlank(vo.getSeasonId())) {
                predicates.add(criteriaBuilder.equal(root.get("seasonId"), vo.getSeasonId()));
            }
            if (StringUtils.isNoneBlank(vo.getYearId())) {
                predicates.add(criteriaBuilder.equal(root.get("yearId"), vo.getYearId()));
            }
            if (StringUtils.isNoneBlank(vo.getSexId())) {
                predicates.add(criteriaBuilder.equal(root.get("sexId"), vo.getSexId()));
            }
            if (StringUtils.isNoneBlank(vo.getSupplierCode())) {
                predicates.add(criteriaBuilder.equal(root.get("supplierCode"), vo.getSupplierCode()));
            }
            if (StringUtils.isNoneBlank(vo.getSupplierName())) {
                predicates.add(criteriaBuilder.equal(root.get("supplierName"), vo.getSupplierName()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        });
    }

}
