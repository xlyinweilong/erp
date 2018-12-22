package com.yin.erp.base.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 生成工具类
 *
 * @author yin.weilong
 * @date 2018.12.14
 */
public class GenerateUtil {

    /**
     * 生成ID
     *
     * @return
     */
    public static String createUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成一个流水号
     *
     * @return
     */
    public static String createSerialNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return date + RandomStringUtils.randomNumeric(4);
    }

}
