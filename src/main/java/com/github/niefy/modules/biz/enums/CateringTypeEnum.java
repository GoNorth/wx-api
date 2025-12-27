package com.github.niefy.modules.biz.enums;

/**
 * 餐饮种类枚举
 */
public enum CateringTypeEnum {
    /**
     * 中餐
     */
    CHINESE("CHINESE"),
    /**
     * 西餐
     */
    WESTERN("WESTERN"),
    /**
     * 日料
     */
    JAPANESE("JAPANESE"),
    /**
     * 火锅
     */
    HOTPOT("HOTPOT"),
    /**
     * 烧烤
     */
    BARBECUE("BARBECUE"),
    /**
     * 快餐
     */
    FASTFOOD("FASTFOOD"),
    /**
     * 小吃
     */
    SNACK("SNACK"),
    /**
     * 其他
     */
    OTHER("OTHER");

    private String code;

    CateringTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static CateringTypeEnum of(String code) {
        for (CateringTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}

