package com.github.niefy.modules.biz.enums;

/**
 * 图片模板状态枚举
 */
public enum ImageTemplateStatusEnum {
    /**
     * 新建阶段
     */
    INIT("INIT"),
    /**
     * 识别阶段
     */
    RECOG("RECOG"),
    /**
     * 产品图片测试
     */
    TEST("TEST"),
    /**
     * 发布使用阶段
     */
    PUBLISH("PUBLISH"),
    /**
     * 作废阶段
     */
    INVALID("INVALID");

    private String value;

    ImageTemplateStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static ImageTemplateStatusEnum of(String value) {
        if (value == null) {
            return null;
        }
        for (ImageTemplateStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }
}

