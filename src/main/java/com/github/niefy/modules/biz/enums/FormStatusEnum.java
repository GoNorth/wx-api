package com.github.niefy.modules.biz.enums;

/**
 * 表单状态枚举
 *
 * @author niefy
 * @date 2026-01-09
 */
public enum FormStatusEnum {
    /**
     * 启用
     */
    ACTIVE("ACTIVE", "启用"),
    /**
     * 禁用
     */
    INACTIVE("INACTIVE", "禁用");

    private String value;
    private String desc;

    FormStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    public static FormStatusEnum of(String value) {
        if (value == null) {
            return null;
        }
        for (FormStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}

