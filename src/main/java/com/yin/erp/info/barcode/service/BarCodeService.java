package com.yin.erp.info.barcode.service;

import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BaseUploadMessage;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.upload.UploadValidateService;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.info.barcode.dao.BarCodeDao;
import com.yin.erp.info.barcode.entity.po.BarCodePo;
import com.yin.erp.info.barcode.entity.vo.BarCodeVo;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.dict.feign.DictFeign;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.bo.GoodsBo;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.goods.feign.GoodsFeign;
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

import javax.persistence.criteria.Predicate;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 条形码服务
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class BarCodeService {

    @Autowired
    private BarCodeDao barCodeDao;
    @Autowired
    private GoodsFeign goodsFeign;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private DictFeign dictFeign;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;
    @Autowired
    private UploadValidateService uploadValidateService;

    /**
     * 保存
     *
     * @param vo
     * @throws Exception
     */
    public void save(BarCodeVo vo) throws MessageException {
        BarCodePo po = new BarCodePo();
        if (StringUtils.isNotBlank(vo.getId())) {
            po = barCodeDao.findById(vo.getId()).get();
        }
        po.setCode(vo.getCode());
        po.setGoodsId(vo.getGoodsId());
        GoodsBo bo = goodsFeign.findGoodsBoById(vo.getGoodsId());
        po.setGoodsCode(bo.getCode());
        po.setGoodsName(bo.getName());
        po.setGoodsColorId(vo.getGoodsColorId());
        po.setGoodsColorCode(dictFeign.getCodeById(vo.getGoodsColorId()));
        po.setGoodsColorName(dictFeign.getCodeById(vo.getGoodsColorId()));
        po.setGoodsSizeId(vo.getGoodsSizeId());
        po.setGoodsSizeName(dictFeign.findSizeNamenById(vo.getGoodsSizeId()));
        barCodeDao.save(po);
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BarCodeVo findById(String id) throws MessageException {
        BarCodePo dictPo = barCodeDao.findById(id).get();
        BarCodeVo dictVo = new BarCodeVo();
        CopyUtil.copyProperties(dictVo, dictPo);
        dictVo.setGoodsCode(dictPo.getGoodsCode());
        return dictVo;
    }

    /**
     * 根据code查询
     *
     * @param code
     * @return
     * @throws MessageException
     */
    public BarCodeVo findByCode(String code, UserSessionBo user) throws MessageException {
        BarCodePo dictPo = barCodeDao.findByCode(code);
        if (dictPo == null) {
            throw new MessageException("不存在条形码：" + code);
        }
        BarCodeVo dictVo = new BarCodeVo();
        CopyUtil.copyProperties(dictVo, dictPo);
        dictVo.setGoodsCode(dictPo.getGoodsCode());
        dictVo.setGoodsName(dictPo.getGoodsName());
        //查询价格
        GoodsPo goodsPo = goodsDao.findByCode(dictPo.getGoodsCode());
        if (goodsPo.getGoodsGroupId() != null && !user.getGoodsGroupIds().contains(goodsPo.getGoodsGroupId())) {
            throw new MessageException("条形码：" + code + "对应的货品，没有操作权限");
        }
        dictVo.setPrice(goodsPo.getTagPrice1());
        dictVo.setTagPrice(goodsPo.getTagPrice1());
        return dictVo;
    }

    /**
     * 查询字典
     *
     * @param vo
     * @return
     */
    public Page<BarCodePo> findDictPage(BarCodeVo vo) {
        Page<BarCodePo> page = barCodeDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNoneBlank(vo.getCode())) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + vo.getCode() + "%"));
            }
            if (StringUtils.isNoneBlank(vo.getGoodsCode())) {
                predicates.add(criteriaBuilder.like(root.get("goodsCode"), "%" + vo.getGoodsCode() + "%"));
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
            barCodeDao.deleteById(id);
        }
    }

    /**
     * 上传条形码
     *
     * @param file
     * @param userSessionBo
     */
    public void updateBarCode(MultipartFile file, UserSessionBo userSessionBo) {
        ValueOperations operations = redisTemplate.opsForValue();
        operations.set(userSessionBo.getId() + ":upload:barcode", new BaseUploadMessage(), 10L, TimeUnit.MINUTES);
        LocalDateTime startTime = LocalDateTime.now();
        try {
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);
            int count = 0;
            List<BarCodePo> list = new ArrayList();
            int errorCellNum = 5;
            boolean success = true;
            for (Row row : sheet) {
                count++;
                operations.set(userSessionBo.getId() + ":upload:barcode", new BaseUploadMessage(sheet.getLastRowNum() + 1, count), 10L, TimeUnit.MINUTES);
                if (count == 1) {
                    row.createCell(errorCellNum).setCellValue("错误信息");
                    continue;
                }
                try {
                    //获取数据
                    String code = ExcelReadUtil.getString(row.getCell(0));
                    String goodsCode = ExcelReadUtil.getString(row.getCell(1));
                    String colorCode = ExcelReadUtil.getString(row.getCell(2));
                    String colorName = ExcelReadUtil.getString(row.getCell(3));
                    String sizeName = ExcelReadUtil.getString(row.getCell(4));

                    if (StringUtils.isBlank(code) || StringUtils.isBlank(goodsCode) || StringUtils.isBlank(colorCode) || StringUtils.isBlank(sizeName)) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "缺少必要数据");
                        success = false;
                        continue;
                    }

                    //验证条形码是否存在
                    if (barCodeDao.findByCode(code) != null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "条形码已经存在");
                        success = false;
                        continue;
                    }
                    //判断货号是否存在
                    GoodsPo g = goodsDao.findByCode(goodsCode);
                    if (g != null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "货号不存在");
                        success = false;
                        continue;
                    }
                    //验证颜色
                    GoodsColorPo goodsColorPo = uploadValidateService.validateGoodsColor(g.getId(), colorCode, colorName, row, errorCellNum);
                    if (goodsColorPo == null) {
                        success = false;
                        continue;
                    }
                    //验证尺码
                    DictSizePo dictSizePo = uploadValidateService.validateGoodsSize(g.getId(), sizeName, row, errorCellNum);
                    if (dictSizePo == null) {
                        success = false;
                        continue;
                    }
                    BarCodePo po = new BarCodePo();
                    po.setCode(code);
                    po.setGoodsId(g.getId());
                    po.setGoodsCode(g.getCode());
                    po.setGoodsName(g.getName());
                    po.setGoodsSizeId(dictSizePo.getId());
                    po.setGoodsSizeName(dictSizePo.getName());
                    po.setGoodsColorId(goodsColorPo.getColorId());
                    po.setGoodsColorCode(goodsColorPo.getColorCode());
                    po.setGoodsColorName(goodsColorPo.getColorName());
                    if (!list.contains(po)) {
                        list.add(po);
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
                barCodeDao.saveAll(list);
                operations.set(userSessionBo.getId() + ":upload:barcode", new BaseUploadMessage(1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
            } else {
                try (FileOutputStream outputStream = new FileOutputStream(erpFileTempUrl + "/" + userSessionBo.getToken() + ".xlsx")) {
                    workbook.write(outputStream);
                    outputStream.flush();
                    operations.set(userSessionBo.getId() + ":upload:barcode", new BaseUploadMessage(userSessionBo.getToken() + ".xlsx", -1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            operations.set(userSessionBo.getId() + ":upload:barcode", new BaseUploadMessage(-1, TimeUtil.useTime(startTime), e.getMessage()), 10L, TimeUnit.MINUTES);
            e.printStackTrace();
        }
    }


}
