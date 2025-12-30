package com.github.niefy.common.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台工具类
 * 用于处理平台 CODE 和描述的转换
 *
 * @author niefy
 * @date 2025-01-01
 */
public class PlatformUtils {

    /**
     * 平台 CODE 到描述的映射
     */
    private static final Map<String, String> PLATFORM_MAP = new HashMap<>();

    /**
     * 内容标签 CODE 到描述的映射
     */
    private static final Map<String, String> CONTENT_TAG_MAP = new HashMap<>();

    static {
        PLATFORM_MAP.put("DOUYIN", "抖音");
        PLATFORM_MAP.put("MEITUAN", "美团");
        PLATFORM_MAP.put("XIAOHONGSHU", "小红书");
        PLATFORM_MAP.put("WECHAT", "微信");

        CONTENT_TAG_MAP.put("SHORTVIDEO", "短视频");
        CONTENT_TAG_MAP.put("GROUPBUY", "团购");
        CONTENT_TAG_MAP.put("STOREVISIT", "到店");
        CONTENT_TAG_MAP.put("NEWPRODUCT", "新品");
    }

    /**
     * 根据平台 CODE 获取平台描述
     * 
     * @param platformCode 平台 CODE，如：DOUYIN, MEITUAN, XIAOHONGSHU, WECHAT
     * @return 平台描述，如：抖音, 美团, 小红书, 微信。如果 CODE 不存在，返回原值
     */
    public static String getPlatformDesc(String platformCode) {
        if (platformCode == null || platformCode.trim().isEmpty()) {
            return null;
        }
        return PLATFORM_MAP.getOrDefault(platformCode.trim().toUpperCase(), platformCode);
    }

    /**
     * 根据内容标签 CODE 获取内容标签描述
     * 
     * @param contentTagCode 内容标签 CODE，如：SHORTVIDEO, GROUPBUY, STOREVISIT, NEWPRODUCT
     * @return 内容标签描述，如：短视频, 团购, 到店, 新品。如果 CODE 不存在，返回原值
     */
    public static String getContentTagDesc(String contentTagCode) {
        if (contentTagCode == null || contentTagCode.trim().isEmpty()) {
            return null;
        }
        return CONTENT_TAG_MAP.getOrDefault(contentTagCode.trim().toUpperCase(), contentTagCode);
    }
}

