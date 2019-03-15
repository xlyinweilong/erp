package com.yin.erp.activity.service;

import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.out.BaseUploadMessage;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.info.goods.dao.GoodsDao;
import com.yin.erp.info.goods.entity.po.GoodsPo;
import com.yin.erp.info.goods.entity.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 活动服务
 *
 * @author yin.weilong
 * @date 2019.03.11
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ActivityService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;

    @Async
    public void uploadGoods(Workbook workbook, UserSessionBo userSessionBo, LocalDateTime startTime, boolean hasPrice) {
        ValueOperations operations = redisTemplate.opsForValue();
        Sheet sheet = workbook.getSheetAt(0);
        int count = 0;
        List<GoodsVo> list = new ArrayList();
        int errorCellNum = hasPrice ? 1 : 2;
        boolean success = true;
        for (Row row : sheet) {
            count++;
            operations.set(userSessionBo.getId() + ":upload:activity", new BaseUploadMessage(sheet.getLastRowNum() + 1, count), 10L, TimeUnit.MINUTES);
            if (count == 1) {
                row.createCell(errorCellNum).setCellValue("错误信息");
                continue;
            }
            try {
                //获取数据
                String goodsCode = ExcelReadUtil.getString(row.getCell(0));
                BigDecimal price = ExcelReadUtil.getBigDecimal(row.getCell(1));

                if (StringUtils.isBlank(goodsCode) || (hasPrice && price == null)) {
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, "缺少必要数据");
                    success = false;
                    continue;
                }

                //判断货号是否存在
                GoodsPo g = goodsDao.findByCode(goodsCode);
                if (g == null) {
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, "货号不存在");
                    success = false;
                    continue;
                }
                if(hasPrice){
                    if (price.compareTo(BigDecimal.ZERO) < 0) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "单价不能小于0");
                        success = false;
                    }
                    if (price.scale() > 2) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "单价最多保留2位小数");
                        success = false;
                    }
                }

                GoodsVo goodsVo = new GoodsVo();
                goodsVo.setId(g.getId());
                goodsVo.setCode(g.getCode());
                goodsVo.setName(g.getName());
                goodsVo.setPrice(price);
                if (list.stream().filter(s -> s.getId().equals(goodsVo.getId())).count() == 0L) {
                    list.add(goodsVo);
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
            operations.set(userSessionBo.getId() + ":upload:activity", new BaseUploadMessage(1, TimeUtil.useTime(startTime), list), 10L, TimeUnit.MINUTES);
        } else {
            try (FileOutputStream outputStream = new FileOutputStream(erpFileTempUrl + "/" + userSessionBo.getToken() + ".xlsx")) {
                workbook.write(outputStream);
                outputStream.flush();
                operations.set(userSessionBo.getId() + ":upload:activity", new BaseUploadMessage(userSessionBo.getToken() + ".xlsx", -1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
