package com.yin.erp.base.utils;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 时间工具
 *
 * @author yin.weilong
 * @date 2018.12.21
 */
public class TimeUtil {

    public static String useTime(LocalDateTime startTime) {
        Duration duration = Duration.between(startTime, LocalDateTime.now());
        long seconds = duration.getSeconds();
        long temp;
        StringBuffer sb = new StringBuffer();
        temp = seconds / 3600;
        if (temp != 0) {
            sb.append(temp).append("小时");
        }
        temp = seconds % 3600 / 60;
        if (temp != 0) {
            sb.append(temp).append("分");
        }
        temp = seconds % 3600 % 60;
        sb.append(temp).append("秒");
        return sb.toString();
    }

}
