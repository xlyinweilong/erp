package com.yin.erp.vip.info.service;

import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.out.BaseUploadMessage;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.base.utils.TimeUtil;
import com.yin.erp.info.channel.dao.ChannelDao;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.employ.dao.EmployDao;
import com.yin.erp.info.employ.entity.po.EmployPo;
import com.yin.erp.vip.balance.dao.VipBalanceLogDao;
import com.yin.erp.vip.balance.entity.po.VipBalanceLogPo;
import com.yin.erp.vip.info.dao.VipDao;
import com.yin.erp.vip.info.entity.po.VipPo;
import com.yin.erp.vip.integral.dao.VipIntegralLogDao;
import com.yin.erp.vip.integral.entity.po.VipIntegralLogPo;
import com.yin.erp.vip.xp.dao.VipXpLogDao;
import com.yin.erp.vip.xp.entity.po.VipXpLogPo;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 会员信息服务
 *
 * @author yin.weilong
 * @date 2019.03.09
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class VipService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private VipDao vipDao;
    @Autowired
    private EmployDao employDao;
    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private VipBalanceLogDao vipBalanceLogDao;
    @Autowired
    private VipIntegralLogDao vipIntegralLogDao;
    @Autowired
    private VipXpLogDao vipXpLogDao;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;


    /**
     * 增加金额日志
     *
     * @param vipId
     * @param vipCode
     * @param balance
     * @param message
     */
    public void addBalanceLog(String vipId, String vipCode, BigDecimal balance, String message) {
        VipBalanceLogPo vipBalanceLogPo = new VipBalanceLogPo();
        vipBalanceLogPo.setBalance(balance);
        vipBalanceLogPo.setMessage(message);
        vipBalanceLogPo.setVipCode(vipCode);
        vipBalanceLogPo.setVipId(vipId);
        vipBalanceLogDao.save(vipBalanceLogPo);
    }

    /**
     * 增加积分日志
     *
     * @param vipId
     * @param vipCode
     * @param integral
     * @param message
     */
    public void addIntegralLog(String vipId, String vipCode, Integer integral, String message) {
        VipIntegralLogPo vipIntegralLogPo = new VipIntegralLogPo();
        vipIntegralLogPo.setIntegral(integral);
        vipIntegralLogPo.setMessage(message);
        vipIntegralLogPo.setVipCode(vipCode);
        vipIntegralLogPo.setVipId(vipId);
        vipIntegralLogDao.save(vipIntegralLogPo);
    }

    /**
     * 增加经验日志
     *
     * @param vipId
     * @param vipCode
     * @param xp
     * @param message
     */
    public void addXpLog(String vipId, String vipCode, Integer xp, String message) {
        VipXpLogPo vipXpLogPo = new VipXpLogPo();
        vipXpLogPo.setMessage(message);
        vipXpLogPo.setVipCode(vipCode);
        vipXpLogPo.setVipId(vipId);
        vipXpLogPo.setXp(xp);
        vipXpLogDao.save(vipXpLogPo);
    }

    /**
     * 上传会员信息
     *
     * @param workbook
     * @param userSessionBo
     * @param startTime
     * @throws Throwable
     */
    @Async
    public void update(Workbook workbook, UserSessionBo userSessionBo, LocalDateTime startTime) throws Throwable {
        ValueOperations operations = redisTemplate.opsForValue();
        Sheet sheet = workbook.getSheetAt(0);
        int count = 0;
        List<VipPo> list = new ArrayList();
        int errorCellNum = 9;
        boolean success = true;
        for (Row row : sheet) {
            count++;
            operations.set(userSessionBo.getId() + ":upload:vip_info", new BaseUploadMessage(sheet.getLastRowNum() + 1, count), 10L, TimeUnit.MINUTES);
            if (count == 1) {
                row.createCell(errorCellNum).setCellValue("错误信息");
                continue;
            }
            try {
                //获取数据
                String vipCode = ExcelReadUtil.getString(row.getCell(0));
                String vipName = ExcelReadUtil.getString(row.getCell(1));
                String vipSexName = ExcelReadUtil.getString(row.getCell(2));
                Date startDate = ExcelReadUtil.getDate(row.getCell(3));
                String employCode = ExcelReadUtil.getString(row.getCell(4));
                String channelCode = ExcelReadUtil.getString(row.getCell(5));
                BigDecimal balance = ExcelReadUtil.getBigDecimal(row.getCell(6), BigDecimal.ZERO);
                Integer integral = ExcelReadUtil.getInteger(row.getCell(7), 0);
                Integer xp = ExcelReadUtil.getInteger(row.getCell(8), 0);

                if (StringUtils.isBlank(vipCode) || StringUtils.isBlank(vipName)) {
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, "缺少必要数据");
                    success = false;
                    continue;
                }

                //验证条形码是否存在
                if (vipDao.findByCode(vipCode) != null || list.stream().filter(v -> v.getCode().equals(vipCode)).count() > 0) {
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, "会员编号已经存在");
                    success = false;
                    continue;
                }

                //性别
                Integer sex = -1;
                if ("男".equals(vipSexName)) {
                    sex = 1;
                } else if ("女".equals(vipSexName)) {
                    sex = 0;
                }

                //开卡营业员
                EmployPo employPo = null;
                if (StringUtils.isNotBlank(employCode)) {
                    employPo = employDao.findByCode(employCode);
                    if (employPo == null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "开卡营业员编号未找到");
                        success = false;
                    }
                }

                //开卡渠道
                ChannelPo channelPo = null;
                if (StringUtils.isNotBlank(channelCode)) {
                    channelPo = channelDao.findByCode(channelCode);
                    if (channelPo == null) {
                        ExcelReadUtil.addErrorToRow(row, errorCellNum, "开卡渠道编号未找到");
                        success = false;
                    }
                }

                if (balance.scale() > 2) {
                    ExcelReadUtil.addErrorToRow(row, errorCellNum, "余额只能保留2位小数");
                    success = false;
                }

                VipPo po = new VipPo();
                po.setCode(vipCode);
                po.setName(vipName);
                po.setBalance(balance);
                po.setXpValue(xp);
                po.setIntegral(integral);
                if (startDate != null) {
                    Instant instant = startDate.toInstant();
                    ZoneId zoneId = ZoneId.systemDefault();
                    LocalDate localDate = instant.atZone(zoneId).toLocalDate();
                    po.setOpenDate(localDate);
                }
                po.setSex(sex);
                if (channelPo != null) {
                    po.setOpenChannelId(channelPo.getId());
                    po.setOpenChannelCode(channelPo.getCode());
                    po.setOpenChannelName(channelPo.getName());
                }
                if (employPo != null) {
                    po.setOpenEmployId(employPo.getId());
                    po.setOpenEmployCode(employPo.getCode());
                    po.setOpenEmployName(employPo.getName());
                }
                list.add(po);
                if (po.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                    this.addBalanceLog(po.getId(), po.getCode(), po.getBalance(), "初始金额");
                }
                if (po.getIntegral() != 0) {
                    this.addIntegralLog(po.getId(), po.getCode(), po.getIntegral(), "初始积分");
                }
                if (po.getXpValue() != 0) {
                    this.addXpLog(po.getId(), po.getCode(), po.getXpValue(), "初始经验");
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
            vipDao.saveAll(list);
            operations.set(userSessionBo.getId() + ":upload:vip_info", new BaseUploadMessage(1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
        } else {
            try (FileOutputStream outputStream = new FileOutputStream(erpFileTempUrl + "/" + userSessionBo.getToken() + ".xlsx")) {
                workbook.write(outputStream);
                outputStream.flush();
                operations.set(userSessionBo.getId() + ":upload:vip_info", new BaseUploadMessage(userSessionBo.getToken() + ".xlsx", -1, TimeUtil.useTime(startTime)), 10L, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
