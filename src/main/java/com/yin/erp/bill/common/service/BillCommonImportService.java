package com.yin.erp.bill.common.service;

import com.yin.erp.base.entity.vo.out.BaseUploadMessage;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.bill.common.dao.BaseBillDao;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BillDetailVo;
import com.yin.erp.bill.common.entity.vo.in.BillGoodsVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.supplier.dao.SupplierDao;
import com.yin.erp.info.supplier.entity.po.SupplierPo;
import com.yin.erp.info.warehouse.dao.WarehouseDao;
import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import com.yin.erp.upload.UploadValidateService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 单据导入服务
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class BillCommonImportService {

    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private SupplierDao supplierDao;
    @Autowired
    private WarehouseDao warehouseDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private UploadValidateService uploadValidateService;
    @Autowired
    private DictSizeDao dictSizeDao;
    @Autowired
    private GoodsColorDao goodsColorDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;

    private Map<String, Map> configMap = new HashMap<String, Map>() {{
        //采购单
        put("com.yin.erp.bill.purchase.service.PurchaseService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 5, 7});
            put("ErrorCellNum", 8);
            put("BillGoodsCode", 2);
            put("BillGoodsColorCode", 3);
            put("BillGoodsColorName", 4);
            put("BillGoodsSizeName", 5);
            put("BillPrice", 6);
            put("BillBillCount", 7);
        }});
        //订货单
        put("com.yin.erp.bill.order.service.OrderService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 5, 7, 9});
            put("ErrorCellNum", 10);
            put("BillWarehouseCode", 2);
            put("BillChannelCode", 3);
            put("BillGoodsCode", 4);
            put("BillGoodsColorCode", 5);
            put("BillGoodsColorName", 6);
            put("BillGoodsSizeName", 7);
            put("BillPrice", 8);
            put("BillBillCount", 9);
        }});
        //配货单
        put("com.yin.erp.bill.delivery.service.DeliveryService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{3, 4, 5, 6, 8, 10});
            put("ErrorCellNum", 11);
            put("BillParentBillCode", 2);
            put("BillWarehouseCode", 3);
            put("BillChannelCode", 4);
            put("BillGoodsCode", 5);
            put("BillGoodsColorCode", 6);
            put("BillGoodsColorName", 7);
            put("BillGoodsSizeName", 8);
            put("BillPrice", 9);
            put("BillBillCount", 10);
        }});
        //厂家来货
        put("com.yin.erp.bill.supplier2warehouse.service.Supplier2WarehouseService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{3, 4, 5, 6, 8, 10});
            put("ErrorCellNum", 11);
            put("BillParentBillCode", 2);
            put("BillSupplierCode", 3);
            put("BillWarehouseCode", 4);
            put("BillGoodsCode", 5);
            put("BillGoodsColorCode", 6);
            put("BillGoodsColorName", 7);
            put("BillGoodsSizeName", 8);
            put("BillPrice", 9);
            put("BillBillCount", 10);
        }});
        //仓库出货
        put("com.yin.erp.bill.warehouse2channel.service.Warehouse2ChannelService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{4, 5, 6, 7, 9, 11});
            put("ErrorCellNum", 12);
            put("BillGrandParentBillCode", 2);
            put("BillParentBillCode", 3);
            put("BillWarehouseCode", 4);
            put("BillChannelCode", 5);
            put("BillGoodsCode", 6);
            put("BillGoodsColorCode", 7);
            put("BillGoodsColorName", 8);
            put("BillGoodsSizeName", 9);
            put("BillPrice", 10);
            put("BillBillCount", 11);
        }});
        //仓库退货
        put("com.yin.erp.bill.warehouse2supplier.service.Warehouse2SupplierService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 5, 7, 9});
            put("ErrorCellNum", 10);
            put("BillWarehouseCode", 2);
            put("BillSupplierCode", 3);
            put("BillGoodsCode", 4);
            put("BillGoodsColorCode", 5);
            put("BillGoodsColorName", 6);
            put("BillGoodsSizeName", 7);
            put("BillPrice", 8);
            put("BillBillCount", 9);
        }});
        //仓库收退货
        put("com.yin.erp.bill.inwarehouse.service.InWarehouseService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 6, 8});
            put("ErrorCellNum", 9);
            put("BillParentBillCode", 2);
            put("BillGoodsCode", 3);
            put("BillGoodsColorCode", 4);
            put("BillGoodsColorName", 5);
            put("BillGoodsSizeName", 6);
            put("BillPrice", 7);
            put("BillBillCount", 8);
        }});
        //仓库盘点
        put("com.yin.erp.bill.warehouseinventory.service.WarehouseInventoryService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 6, 8});
            put("ErrorCellNum", 9);
            put("BillWarehouseCode", 2);
            put("BillGoodsCode", 3);
            put("BillGoodsColorCode", 4);
            put("BillGoodsColorName", 5);
            put("BillGoodsSizeName", 6);
            put("BillPrice", 7);
            put("BillBillCount", 8);
        }});
        //仓库损益
        put("com.yin.erp.bill.warehouseloss.service.WarehouseLossService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 6, 8});
            put("ErrorCellNum", 9);
            put("BillWarehouseCode", 2);
            put("BillGoodsCode", 3);
            put("BillGoodsColorCode", 4);
            put("BillGoodsColorName", 5);
            put("BillGoodsSizeName", 6);
            put("BillPrice", 7);
            put("BillBillCount", 8);
        }});
        //渠道收货
        put("com.yin.erp.bill.inchannel.service.InChannelService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 6, 8});
            put("ErrorCellNum", 9);
            put("BillParentBillCode", 2);
            put("BillGoodsCode", 3);
            put("BillGoodsColorCode", 4);
            put("BillGoodsColorName", 5);
            put("BillGoodsSizeName", 6);
            put("BillPrice", 7);
            put("BillBillCount", 8);
        }});
        //渠道调出
        put("com.yin.erp.bill.channel2channelout.service.Channel2ChannelOutService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{3, 4, 5, 6, 8, 10});
            put("ErrorCellNum", 11);
            put("BillParentBillCode", 2);
            put("BillChannelCode", 3);
            put("BillToChannelCode", 4);
            put("BillGoodsCode", 5);
            put("BillGoodsColorCode", 6);
            put("BillGoodsColorName", 7);
            put("BillGoodsSizeName", 8);
            put("BillPrice", 9);
            put("BillBillCount", 10);
        }});
        //渠道调入
        put("com.yin.erp.bill.channel2channelin.service.Channel2ChannelInService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 6, 8});
            put("ErrorCellNum", 9);
            put("BillParentBillCode", 2);
            put("BillGoodsCode", 3);
            put("BillGoodsColorCode", 4);
            put("BillGoodsColorName", 5);
            put("BillGoodsSizeName", 6);
            put("BillPrice", 7);
            put("BillBillCount", 8);
        }});
        //渠道退货退货
        put("com.yin.erp.bill.channel2warehouse.service.Channel2WarehouseService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 5, 7, 9});
            put("ErrorCellNum", 10);
            put("BillChannelCode", 2);
            put("BillWarehouseCode", 3);
            put("BillGoodsCode", 4);
            put("BillGoodsColorCode", 5);
            put("BillGoodsColorName", 6);
            put("BillGoodsSizeName", 7);
            put("BillPrice", 8);
            put("BillBillCount", 9);
        }});
        //渠道采购收货
        put("com.yin.erp.bill.supplier2channel.service.Supplier2ChannelService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 5, 7, 9});
            put("ErrorCellNum", 10);
            put("BillSupplierCode", 2);
            put("BillChannelCode", 3);
            put("BillGoodsCode", 4);
            put("BillGoodsColorCode", 5);
            put("BillGoodsColorName", 6);
            put("BillGoodsSizeName", 7);
            put("BillPrice", 8);
            put("BillBillCount", 9);
        }});
        //渠道采购退货
        put("com.yin.erp.bill.channel2supplier.service.Channel2SupplierService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 5, 7, 9});
            put("ErrorCellNum", 10);
            put("BillChannelCode", 2);
            put("BillSupplierCode", 3);
            put("BillGoodsCode", 4);
            put("BillGoodsColorCode", 5);
            put("BillGoodsColorName", 6);
            put("BillGoodsSizeName", 7);
            put("BillPrice", 8);
            put("BillBillCount", 9);
        }});
        //渠道盘点
        put("com.yin.erp.bill.channelinventory.service.ChannelInventoryService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 6, 8});
            put("ErrorCellNum", 9);
            put("BillChannelCode", 2);
            put("BillGoodsCode", 3);
            put("BillGoodsColorCode", 4);
            put("BillGoodsColorName", 5);
            put("BillGoodsSizeName", 6);
            put("BillPrice", 7);
            put("BillBillCount", 8);
        }});
        //渠道损益
        put("com.yin.erp.bill.channelloss.service.ChannelLossService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 6, 8});
            put("ErrorCellNum", 9);
            put("BillChannelCode", 2);
            put("BillGoodsCode", 3);
            put("BillGoodsColorCode", 4);
            put("BillGoodsColorName", 5);
            put("BillGoodsSizeName", 6);
            put("BillPrice", 7);
            put("BillBillCount", 8);
        }});
        //渠道调出通知单
        put("com.yin.erp.bill.noticechannel2channelout.service.NoticeChannel2ChannelOutService", new HashMap<String, Object>() {{
            put("MastNotNullIndex", new int[]{2, 3, 4, 5, 7, 9});
            put("ErrorCellNum", 10);
            put("BillChannelCode", 2);
            put("BillToChannelCode", 3);
            put("BillGoodsCode", 4);
            put("BillGoodsColorCode", 5);
            put("BillGoodsColorName", 6);
            put("BillGoodsSizeName", 7);
            put("BillPrice", 8);
            put("BillBillCount", 9);
        }});
    }};


    /**
     * 获取excel的字符串
     *
     * @param billService
     * @param row
     * @param key
     * @return
     * @throws MessageException
     */
    private String getExcelString(BillService billService, Row row, String key) throws Exception {
        Map map = configMap.get(billService.getClass().getName());
        String str = map.containsKey(key) ? ExcelReadUtil.getString(row.getCell((Integer) map.get(key))) : null;
        if (str == null && map.containsKey(key)) {
            isMastNotNullHasNull(map, (Integer) map.get(key));
        }
        return str;
    }

    /**
     * 获取excel的小数
     *
     * @param billService
     * @param row
     * @param key
     * @return
     * @throws MessageException
     */
    private BigDecimal getExcelBigDecimal(BillService billService, Row row, String key) throws Exception {
        Map map = configMap.get(billService.getClass().getName());
        BigDecimal b = map.containsKey(key) ? ExcelReadUtil.getBigDecimal(row.getCell((Integer) map.get(key))) : null;
        if (b == null && map.containsKey(key)) {
            isMastNotNullHasNull(map, (Integer) map.get(key));
        }
        return b;
    }

    /**
     * 获取excel的整数
     *
     * @param billService
     * @param row
     * @param key
     * @return
     * @throws MessageException
     */
    private Integer getExcelInteger(BillService billService, Row row, String key) throws Exception {
        Map map = configMap.get(billService.getClass().getName());
        Integer r = map.containsKey(key) ? ExcelReadUtil.getInteger(row.getCell((Integer) map.get(key))) : null;
        if (r == null && map.containsKey(key)) {
            isMastNotNullHasNull(map, (Integer) map.get(key));
        }
        return r;
    }

    /**
     * 判断是否确实必要参数
     *
     * @param map
     * @param i
     * @throws MessageException
     */
    private void isMastNotNullHasNull(Map map, int i) throws Exception {
        int[] array = (int[]) map.get("MastNotNullIndex");
        for (int s : array) {
            if (s == i) {
                throw new MessageException("缺少必要数据");
            }
        }
    }

    /**
     * 获取错误cell的编号
     *
     * @param billService
     * @return
     * @throws MessageException
     */
    private Integer getErrorCellNum(BillService billService) throws MessageException {
        Map map = configMap.get(billService.getClass().getName());
        return (int) map.get("ErrorCellNum");
    }

    /**
     * 上传单据复用
     *
     * @param workbook
     * @param userSessionBo
     * @param billService
     * @param redisKey
     */
    @Async
    public void uploadBill(Workbook workbook, UserSessionBo userSessionBo, BillService billService, String redisKey, BaseBillDao parentBaseBillDao, BaseBillDao grandParentBaseBillDao, LocalDateTime startTime) throws Throwable {
        ValueOperations operations = redisTemplate.opsForValue();
        Sheet sheet = workbook.getSheetAt(0);
        int count = 0;
        //准备数据，多个单据
        List<BillVo> list = new ArrayList<>();
        //中间状态
        boolean success = true;
        int errorCellNum = getErrorCellNum(billService);
        for (Row row : sheet) {
            count++;
            operations.set(userSessionBo.getId() + ":upload:bill:" + redisKey, new BaseUploadMessage(sheet.getLastRowNum() + 1, count), 10L, TimeUnit.MINUTES);
            if (count == 1) {
                row.createCell(errorCellNum).setCellValue("错误信息");
                continue;
            }
            try {
                //获取数据
                String manualCode = ExcelReadUtil.getString(row.getCell(0));
                Date date = ExcelReadUtil.getDate(row.getCell(1));
                String gradParentBillCode = getExcelString(billService, row, "BillGrandParentBillCode");
                String parentBillCode = getExcelString(billService, row, "BillParentBillCode");
                String channelCode = getExcelString(billService, row, "BillChannelCode");
                String toChannelCode = getExcelString(billService, row, "BillToChannelCode");
                String supplierCode = getExcelString(billService, row, "BillSupplierCode");
                String warehouseCode = getExcelString(billService, row, "BillWarehouseCode");
                String goodsCode = getExcelString(billService, row, "BillGoodsCode");
                String goodsColorCode = getExcelString(billService, row, "BillGoodsColorCode");
                String goodsColorName = getExcelString(billService, row, "BillGoodsColorName");
                String goodsSizeName = getExcelString(billService, row, "BillGoodsSizeName");
                BigDecimal price = getExcelBigDecimal(billService, row, "BillPrice");
                Integer billCount = getExcelInteger(billService, row, "BillBillCount");

                if (date == null || manualCode == null) {
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, "缺少必要数据");
                    success = false;
                    continue;
                }

                //数据VO
                BillVo vo = null;
                List<BillGoodsVo> billGoodsList = null;
                //根据手工单号获取单子
                Optional<BillVo> optionalVo = list.stream().filter(r -> r.getManualCode().equals(manualCode)).findFirst();
                if (optionalVo.isPresent()) {
                    vo = optionalVo.get();
                    billGoodsList = vo.getGoodsList();
                } else {
                    vo = new BillVo();
                    vo.setStatus(BillStatusEnum.PENDING.name());
                    vo.setManualCode(manualCode);
                    billGoodsList = new ArrayList<>();
                    vo.setGoodsList(billGoodsList);
                    list.add(vo);
                }

                //如果没设置设置，设置一个时间
                if (vo.getBillDate() == null) {
                    vo.setBillDate(date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }

                //通过上上游单据设置
                if (!setGrandParent(vo, gradParentBillCode, row, errorCellNum, grandParentBaseBillDao)) {
                    success = false;
                    continue;
                }

                //通过上游单据设置
                if (!setParent(vo, parentBillCode, row, errorCellNum, parentBaseBillDao)) {
                    success = false;
                    continue;
                }

                //设置供应商、仓库、渠道
                if (!setSupplierAndWarehouseAndChannel(vo, supplierCode, warehouseCode, channelCode, toChannelCode, row, errorCellNum)) {
                    success = false;
                }

                //判断是否匹配货号、颜色、内长、尺码
                GoodsPo goodsPo = goodsDao.findByCode(goodsCode);
                if (goodsPo == null) {
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, "货号不存在");
                    continue;
                }
                //颜色
                GoodsColorPo goodsColorPo = uploadValidateService.validateGoodsColor(goodsPo.getId(), goodsColorCode, goodsColorName, row, errorCellNum);
                if (goodsColorPo == null) {
                    success = false;
                    continue;
                }
                String colorId = goodsColorPo.getColorId();
                //验证尺码
                DictSizePo dictSizePo = uploadValidateService.validateGoodsSize(goodsPo.getSizeGroupId(), goodsSizeName, row, errorCellNum);
                if (dictSizePo == null) {
                    success = false;
                    continue;
                }
                //重复校验
                if (billGoodsList.stream().filter(g -> g.getDetail() != null && g.getGoodsId().equals(goodsPo.getId())
                        && g.getDetail().stream().filter(d -> d.getColorId().equals(colorId) && d.getSizeId().equals(dictSizePo.getId())).count() > 0).count() > 0) {
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, "货号、颜色、内长、尺码在文件中已经存在");
                    success = false;
                }
                if (price == null) {
                    price = goodsPo.getTagPrice1();
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
                    billGoodsVo.setTagPrice(goodsPo.getTagPrice1());
                    billGoodsList.add(billGoodsVo);
                    detailList = new ArrayList<>();
                    billGoodsVo.setDetail(detailList);
                }
                BillDetailVo billDetailVo = new BillDetailVo();
                billDetailVo.setSizeId(dictSizePo.getId());
                billDetailVo.setColorId(goodsColorPo.getColorId());
                billDetailVo.setBillCount(billCount);
                detailList.add(billDetailVo);
            } catch (MessageException e) {
                ExcelReadUtil.addErrorToRow(row, errorCellNum, e.getMessage());
                success = false;
            } catch (Exception e) {
                success = false;
                e.printStackTrace();
            }
        }
        //写入数据库
        this.saveData(success, list, billService, userSessionBo, operations, redisKey, startTime, workbook);

    }

    /**
     * 写入数据库
     *
     * @param success
     * @param list
     * @param billService
     * @param userSessionBo
     * @param operations
     * @param redisKey
     * @param startTime
     * @param workbook
     * @throws Exception
     */
    private void saveData(boolean success, List<BillVo> list, BillService billService, UserSessionBo userSessionBo, ValueOperations operations, String redisKey, LocalDateTime startTime, Workbook workbook) throws Exception {
        if (success) {
            for (BillVo vo : list) {
                billService.save(vo, userSessionBo);
            }
            operations.set(userSessionBo.getId() + ":upload:bill:" + redisKey, new BaseUploadMessage(1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
        } else {
            try (FileOutputStream outputStream = new FileOutputStream(erpFileTempUrl + "/" + userSessionBo.getToken() + ".xlsx")) {
                workbook.write(outputStream);
                outputStream.flush();
                operations.set(userSessionBo.getId() + ":upload:bill:" + redisKey, new BaseUploadMessage(userSessionBo.getToken() + ".xlsx", -1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }


    /**
     * 通过上游单据设置
     *
     * @param vo
     * @param parentBillCode
     * @param row
     * @param errorCellNum
     * @param parentBaseBillDao
     * @return
     */
    private boolean setParent(BillVo vo, String parentBillCode, Row row, int errorCellNum, BaseBillDao parentBaseBillDao) {
        if (vo.getParentBillId() == null && parentBillCode != null) {
            BillPo billPo = parentBaseBillDao.findByCode(parentBillCode);
            if (billPo == null) {
                ExcelReadUtil.addErrorToRow(row, errorCellNum, "上游单据没找到");
                return false;
            }
            if (!billPo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                ExcelReadUtil.addErrorToRow(row, errorCellNum, "上游单据不是审核成功状态");
                return false;
            }
            vo.setGrandParentBillId(billPo.getParentBillId());
            vo.setGrandParentBillCode(billPo.getParentBillCode());
            vo.setParentBillId(billPo.getId());
            vo.setParentBillCode(billPo.getCode());
            vo.setWarehouseId(billPo.getWarehouseId());
            vo.setWarehouseCode(billPo.getWarehouseCode());
            vo.setWarehouseName(billPo.getWarehouseName());
            vo.setChannelId(billPo.getChannelId());
            vo.setChannelCode(billPo.getChannelCode());
            vo.setChannelName(billPo.getChannelName());
            vo.setToChannelId(billPo.getToChannelId());
            vo.setToChannelCode(billPo.getToChannelCode());
            vo.setToChannelName(billPo.getToChannelName());
            vo.setSupplierId(billPo.getSupplierId());
            vo.setSupplierName(billPo.getSupplierName());
            vo.setSupplierCode(billPo.getSupplierCode());
        }
        return true;
    }

    /**
     * 通过上上游单据设置
     *
     * @param vo
     * @param grandParentBillCode
     * @param row
     * @param errorCellNum
     * @param grandParentBaseBillDao
     * @return
     */
    private boolean setGrandParent(BillVo vo, String grandParentBillCode, Row row, int errorCellNum, BaseBillDao grandParentBaseBillDao) {
        if (vo.getGrandParentBillId() == null && grandParentBillCode != null) {
            BillPo billPo = grandParentBaseBillDao.findByCode(grandParentBillCode);
            if (billPo == null) {
                ExcelReadUtil.addErrorToRow(row, errorCellNum, "上上游单据没找到");
                return false;
            }
            if (!billPo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                ExcelReadUtil.addErrorToRow(row, errorCellNum, "上上游单据不是审核成功状态");
                return false;
            }
            vo.setGrandParentBillId(billPo.getId());
            vo.setGrandParentBillCode(billPo.getCode());
            vo.setWarehouseId(billPo.getWarehouseId());
            vo.setWarehouseCode(billPo.getWarehouseCode());
            vo.setWarehouseName(billPo.getWarehouseName());
            vo.setChannelId(billPo.getChannelId());
            vo.setChannelCode(billPo.getChannelCode());
            vo.setChannelName(billPo.getChannelName());
            vo.setToChannelId(billPo.getToChannelId());
            vo.setToChannelCode(billPo.getToChannelCode());
            vo.setToChannelName(billPo.getToChannelName());
            vo.setSupplierId(billPo.getSupplierId());
            vo.setSupplierName(billPo.getSupplierName());
            vo.setSupplierCode(billPo.getSupplierCode());
        }
        return true;
    }

    /**
     * 设置供应商、仓库、渠道
     *
     * @param vo
     * @param supplierCode
     * @param warehouseCode
     * @param channelCode
     * @param toChannelCode
     * @param row
     * @param errorCellNum
     * @return
     */
    private boolean setSupplierAndWarehouseAndChannel(BillVo vo, String supplierCode, String warehouseCode, String channelCode, String toChannelCode, Row row, int errorCellNum) {
        boolean success = true;
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
                ExcelReadUtil.addErrorToRow(row, errorCellNum, "仓库没找到");
                success = false;
            }
            vo.setWarehouseId(warehousePo.getId());
        }
        if (vo.getChannelId() == null && channelCode != null) {
            ChannelPo channelPo = channelDao.findByCode(channelCode);
            if (channelPo == null) {
                ExcelReadUtil.addErrorToRow(row, errorCellNum, "渠道没找到");
                success = false;
            }
            vo.setChannelId(channelPo.getId());
        }
        if (vo.getToChannelId() == null && toChannelCode != null) {
            ChannelPo channelPo = channelDao.findByCode(toChannelCode);
            if (channelPo == null) {
                ExcelReadUtil.addErrorToRow(row, errorCellNum, "入渠道没找到");
                success = false;
            }
            vo.setToChannelId(channelPo.getId());
        }

        return success;
    }


}
