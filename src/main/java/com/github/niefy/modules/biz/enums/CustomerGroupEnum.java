package com.github.niefy.modules.biz.enums;

/**
 * 客户人群枚举
 */
public enum CustomerGroupEnum {
    /**
     * 学生
     */
    STUDENT("STUDENT"),
    /**
     * 上班族
     */
    OFFICE_WORKER("OFFICE_WORKER"),
    /**
     * 家庭
     */
    FAMILY("FAMILY"),
    /**
     * 商务人士
     */
    BUSINESS("BUSINESS"),
    /**
     * 老年人
     */
    ELDERLY("ELDERLY"),
    /**
     * 其他
     */
    OTHER("OTHER");

    private String code;

    CustomerGroupEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static CustomerGroupEnum of(String code) {
        for (CustomerGroupEnum group : values()) {
            if (group.code.equals(code)) {
                return group;
            }
        }
        return null;
    }
}

