package com.github.niefy.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 日期键工具类
 * 用于处理计划项目表中的 dateKey 字段转换
 *
 * @author niefy
 * @date 2025-01-01
 */
public class DateKeyUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 将日期字符串转换为周格式
     * 例如："2025-12-30" -> "WEEK1"
     * 
     * @param dateKey 日期字符串，格式：yyyy-MM-dd
     * @return 周格式字符串，例如：WEEK1, WEEK2, WEEK3, WEEK4
     * @throws IllegalArgumentException 如果日期格式不正确
     */
    public static String convertDateToWeek(String dateKey) {
        if (dateKey == null || dateKey.trim().isEmpty()) {
            throw new IllegalArgumentException("dateKey cannot be null or empty");
        }

        try {
            // 解析日期字符串
            LocalDate date = LocalDate.parse(dateKey.trim(), DATE_FORMATTER);
            
            // 计算该日期是所在月份的第几周
            // 方法：计算该日期距离月初的天数，除以7向上取整
            int dayOfMonth = date.getDayOfMonth();
            int weekInMonth = (dayOfMonth - 1) / 7 + 1;
            
            // 确保周数至少为1
            if (weekInMonth < 1) {
                weekInMonth = 1;
            }
            
            return "WEEK" + weekInMonth;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd, but got: " + dateKey, e);
        }
    }

    /**
     * 根据策略类型转换日期键
     * 当 strategyType 为 2（周计划表）时，将日期字符串转换为周格式
     * 
     * @param dateKey 日期字符串，格式：yyyy-MM-dd
     * @param strategyType 策略类型：1-私域复购，2-公域获客（周计划表）
     * @return 转换后的日期键
     */
    public static String convertDateKeyByStrategy(String dateKey, Integer strategyType) {
        if (dateKey == null || dateKey.trim().isEmpty()) {
            return dateKey;
        }
        
        // 如果 strategyType 为 2（周计划表），则转换为周格式
        if (strategyType != null && strategyType == 2) {
            return convertDateToWeek(dateKey);
        }
        
        // 其他情况直接返回原值
        return dateKey;
    }
}

